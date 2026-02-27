package com.example.habittracker.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.habittracker.data.model.Habit
import com.example.habittracker.ui.HabitViewModel
import com.example.habittracker.ui.components.getHabitColor
import com.example.habittracker.ui.components.getHabitIcon
import com.example.habittracker.ui.components.habitColors
import com.example.habittracker.ui.components.habitIcons

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditHabitScreen(
    viewModel: HabitViewModel,
    habitId: Long? = null,
    onNavigateBack: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedIconName by remember { mutableStateOf("FitnessCenter") }
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    var targetDays by remember { mutableFloatStateOf(7f) }
    var existingHabit by remember { mutableStateOf<Habit?>(null) }

    val isEditing = habitId != null

    LaunchedEffect(habitId) {
        if (habitId != null) {
            val habit = viewModel.habitsWithCompletions.value
                .find { it.habit.id == habitId }?.habit
            habit?.let {
                existingHabit = it
                name = it.name
                description = it.description
                selectedIconName = it.iconName
                selectedColorIndex = it.colorIndex
                targetDays = it.targetDaysPerWeek.toFloat()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Habit" else "New Habit",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {
                    if (name.isNotBlank()) {
                        val habit = Habit(
                            id = existingHabit?.id ?: 0,
                            name = name.trim(),
                            description = description.trim(),
                            iconName = selectedIconName,
                            colorIndex = selectedColorIndex,
                            targetDaysPerWeek = targetDays.toInt(),
                            createdAt = existingHabit?.createdAt ?: System.currentTimeMillis(),
                        )
                        if (isEditing) viewModel.updateHabit(habit) else viewModel.addHabit(habit)
                        onNavigateBack()
                    }
                },
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                shape = MaterialTheme.shapes.large,
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text(
                    if (isEditing) "Save" else "Create",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
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

            // Preview card showing the selected icon and color
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                val animatedColor by animateColorAsState(
                    targetValue = getHabitColor(selectedColorIndex),
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioLowBouncy,
                        stiffness = Spring.StiffnessLow,
                    ),
                    label = "color",
                )
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(MaterialTheme.shapes.large)
                        .background(animatedColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = getHabitIcon(selectedIconName),
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = animatedColor,
                    )
                }
            }

            // Name field
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Habit Name") },
                placeholder = { Text("e.g. Morning Run") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )

            Spacer(Modifier.height(16.dp))

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                placeholder = { Text("e.g. Run 5km every morning") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                ),
            )

            Spacer(Modifier.height(24.dp))

            // Icon selection
            Text(
                text = "Icon",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(12.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                habitIcons.forEach { habitIcon ->
                    val isSelected = habitIcon.name == selectedIconName
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedIconName = habitIcon.name },
                        label = { Text(habitIcon.label) },
                        leadingIcon = {
                            Icon(
                                habitIcon.icon,
                                contentDescription = null,
                                modifier = Modifier.size(FilterChipDefaults.IconSize),
                            )
                        },
                        shape = MaterialTheme.shapes.small,
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Color selection
            Text(
                text = "Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(12.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                habitColors.forEachIndexed { index, color ->
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .then(
                                if (index == selectedColorIndex)
                                    Modifier.border(3.dp, MaterialTheme.colorScheme.onSurface, CircleShape)
                                else Modifier
                            )
                            .clickable { selectedColorIndex = index },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (index == selectedColorIndex) {
                            Icon(
                                Icons.Filled.Check,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.size(20.dp),
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Target days per week
            Text(
                text = "Target: ${targetDays.toInt()} days per week",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.height(8.dp))
            Slider(
                value = targetDays,
                onValueChange = { targetDays = it },
                valueRange = 1f..7f,
                steps = 5,
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(Modifier.height(100.dp))
        }
    }
}
