package com.example.habittracker

import android.app.Application
import com.example.habittracker.data.HabitDatabase
import com.example.habittracker.data.repository.HabitRepository

class HabitTrackerApp : Application() {

    val database by lazy { HabitDatabase.getDatabase(this) }
    val repository by lazy { HabitRepository(database.habitDao()) }
}
