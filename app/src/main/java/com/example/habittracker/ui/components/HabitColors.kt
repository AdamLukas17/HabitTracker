package com.example.habittracker.ui.components

import androidx.compose.ui.graphics.Color
import com.example.habittracker.ui.theme.HabitColorCreativity
import com.example.habittracker.ui.theme.HabitColorFitness
import com.example.habittracker.ui.theme.HabitColorHealth
import com.example.habittracker.ui.theme.HabitColorLearning
import com.example.habittracker.ui.theme.HabitColorMindfulness
import com.example.habittracker.ui.theme.HabitColorProductivity
import com.example.habittracker.ui.theme.HabitColorSelfCare
import com.example.habittracker.ui.theme.HabitColorSocial

val habitColors: List<Color> = listOf(
    HabitColorHealth,
    HabitColorFitness,
    HabitColorMindfulness,
    HabitColorLearning,
    HabitColorProductivity,
    HabitColorSocial,
    HabitColorCreativity,
    HabitColorSelfCare,
)

fun getHabitColor(index: Int): Color =
    habitColors[index.coerceIn(0, habitColors.lastIndex)]
