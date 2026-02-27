package com.example.habittracker.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.model.HabitWithCompletions
import com.example.habittracker.ui.HabitViewModel
import com.example.habittracker.ui.components.getHabitColor
import com.example.habittracker.ui.components.getHabitIcon
import com.example.habittracker.ui.dateToMillis
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitDetailScreen(
    viewModel: HabitViewModel,
    habitId: Long,
    onNavigateBack: () -> Unit,
    onEditHabit: (Long) -> Unit,
) {
    LaunchedEffect(habitId) {
        viewModel.selectHabit(habitId)
    }

    val habitWithCompletions by viewModel.selectedHabit.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val hwc = habitWithCompletions ?: return

    val habit = hwc.habit
    val habitColor = getHabitColor(habit.colorIndex)
    val completionDates = hwc.completions.map {
        java.time.Instant.ofEpochMilli(it.dateMillis)
            .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
    }.toSet()

    val currentStreak = calculateStreak(completionDates)
    val totalCompletions = hwc.completions.size
    val thisWeekCompletions = countThisWeekCompletions(completionDates)
    val weeklyProgress = thisWeekCompletions.toFloat() / habit.targetDaysPerWeek.coerceAtLeast(1)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(habit.name, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { onEditHabit(habitId) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
        ) {
            Spacer(Modifier.height(8.dp))

            // Hero card with icon and streak
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = habitColor.copy(alpha = 0.1f),
                ),
                shape = MaterialTheme.shapes.extraLarge,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(MaterialTheme.shapes.large)
                            .background(habitColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = getHabitIcon(habit.iconName),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp),
                            tint = habitColor,
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    if (habit.description.isNotBlank()) {
                        Text(
                            text = habit.description,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                        Spacer(Modifier.height(16.dp))
                    }

                    // Weekly progress bar
                    Text(
                        text = "This Week: $thisWeekCompletions / ${habit.targetDaysPerWeek} days",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(Modifier.height(8.dp))
                    val animatedProgress by animateFloatAsState(
                        targetValue = weeklyProgress.coerceAtMost(1f),
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow,
                        ),
                        label = "progress",
                    )
                    LinearProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small),
                        color = habitColor,
                        trackColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                        strokeCap = StrokeCap.Round,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                StatCard(
                    icon = Icons.Filled.LocalFireDepartment,
                    value = "$currentStreak",
                    label = "Current Streak",
                    color = habitColor,
                    modifier = Modifier.weight(1f),
                )
                StatCard(
                    icon = Icons.Filled.TrendingUp,
                    value = "$totalCompletions",
                    label = "Total Check-ins",
                    color = habitColor,
                    modifier = Modifier.weight(1f),
                )
            }

            Spacer(Modifier.height(24.dp))

            // Calendar view
            Text(
                text = "Calendar",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(12.dp))
            CalendarView(
                habitId = habitId,
                completionDates = completionDates,
                habitColor = habitColor,
                onToggleDate = { date -> viewModel.toggleCompletion(habitId, date) },
            )

            Spacer(Modifier.height(24.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Habit") },
            text = { Text("Are you sure you want to delete \"${habit.name}\"? This will remove all tracking data.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteHabit(habit)
                        showDeleteDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            shape = MaterialTheme.shapes.extraLarge,
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        shape = MaterialTheme.shapes.large,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp),
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun CalendarView(
    habitId: Long,
    completionDates: Set<LocalDate>,
    habitColor: androidx.compose.ui.graphics.Color,
    onToggleDate: (LocalDate) -> Unit,
) {
    val currentMonth = YearMonth.now()
    val firstDay = currentMonth.atDay(1)
    val daysInMonth = currentMonth.lengthOfMonth()
    val startDayOfWeek = firstDay.dayOfWeek.value % 7 // Sunday = 0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Month header
            Text(
                text = "${currentMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${currentMonth.year}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 12.dp),
            )

            // Day of week headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Calendar grid
            val totalCells = startDayOfWeek + daysInMonth
            val rows = (totalCells + 6) / 7

            for (row in 0 until rows) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayNumber = cellIndex - startDayOfWeek + 1

                        if (dayNumber in 1..daysInMonth) {
                            val date = currentMonth.atDay(dayNumber)
                            val isCompleted = completionDates.contains(date)
                            val isToday = date == LocalDate.now()
                            val isFuture = date.isAfter(LocalDate.now())

                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1f)
                                    .padding(2.dp)
                                    .clip(CircleShape)
                                    .then(
                                        when {
                                            isCompleted -> Modifier.background(habitColor)
                                            isToday -> Modifier.background(
                                                MaterialTheme.colorScheme.surfaceContainerHigh
                                            )
                                            else -> Modifier
                                        }
                                    )
                                    .then(
                                        if (!isFuture) Modifier.clickable { onToggleDate(date) }
                                        else Modifier
                                    ),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = dayNumber.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = when {
                                        isCompleted -> MaterialTheme.colorScheme.surface
                                        isFuture -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                                        else -> MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                )
                            }
                        } else {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

private fun calculateStreak(completionDates: Set<LocalDate>): Int {
    if (completionDates.isEmpty()) return 0
    val sorted = completionDates.sortedDescending()
    var streak = 0
    var current = LocalDate.now()

    if (!sorted.contains(current)) {
        current = current.minusDays(1)
    }

    for (date in sorted) {
        if (date == current) {
            streak++
            current = current.minusDays(1)
        } else if (date.isBefore(current)) {
            break
        }
    }
    return streak
}

private fun countThisWeekCompletions(completionDates: Set<LocalDate>): Int {
    val today = LocalDate.now()
    val startOfWeek = today.minusDays(today.dayOfWeek.value.toLong() % 7)
    return completionDates.count { it in startOfWeek..today }
}
