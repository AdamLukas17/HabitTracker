package com.example.habittracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

data class HabitIcon(val name: String, val icon: ImageVector, val label: String)

val habitIcons = listOf(
    HabitIcon("FitnessCenter", Icons.Filled.FitnessCenter, "Exercise"),
    HabitIcon("DirectionsRun", Icons.Filled.DirectionsRun, "Running"),
    HabitIcon("SelfImprovement", Icons.Filled.SelfImprovement, "Meditate"),
    HabitIcon("AutoStories", Icons.Filled.AutoStories, "Reading"),
    HabitIcon("LocalDrink", Icons.Filled.LocalDrink, "Water"),
    HabitIcon("Restaurant", Icons.Filled.Restaurant, "Healthy Eating"),
    HabitIcon("Nightlight", Icons.Filled.Nightlight, "Sleep"),
    HabitIcon("Code", Icons.Filled.Code, "Coding"),
    HabitIcon("Brush", Icons.Filled.Brush, "Art"),
    HabitIcon("MusicNote", Icons.Filled.MusicNote, "Music"),
    HabitIcon("Spa", Icons.Filled.Spa, "Self-Care"),
    HabitIcon("Star", Icons.Filled.Star, "General"),
)

fun getHabitIcon(name: String): ImageVector =
    habitIcons.find { it.name == name }?.icon ?: Icons.Filled.Star
