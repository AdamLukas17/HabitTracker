package com.example.habittracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class Habit(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val iconName: String = "FitnessCenter",
    val colorIndex: Int = 0,
    val targetDaysPerWeek: Int = 7,
    val createdAt: Long = System.currentTimeMillis(),
)
