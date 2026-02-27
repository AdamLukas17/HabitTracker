package com.example.habittracker.data.repository

import com.example.habittracker.data.dao.HabitDao
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitCompletion
import com.example.habittracker.data.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

class HabitRepository(private val habitDao: HabitDao) {

    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>> =
        habitDao.getAllHabitsWithCompletions()

    fun getHabitWithCompletions(habitId: Long): Flow<HabitWithCompletions?> =
        habitDao.getHabitWithCompletions(habitId)

    suspend fun getHabitById(habitId: Long): Habit? =
        habitDao.getHabitById(habitId)

    suspend fun insertHabit(habit: Habit): Long =
        habitDao.insertHabit(habit)

    suspend fun updateHabit(habit: Habit) =
        habitDao.updateHabit(habit)

    suspend fun deleteHabit(habit: Habit) =
        habitDao.deleteHabit(habit)

    suspend fun toggleCompletion(habitId: Long, dateMillis: Long) {
        val isCompleted = habitDao.isCompletedOn(habitId, dateMillis) > 0
        if (isCompleted) {
            habitDao.deleteCompletion(habitId, dateMillis)
        } else {
            habitDao.insertCompletion(HabitCompletion(habitId = habitId, dateMillis = dateMillis))
        }
    }

    fun getTotalCompletions(habitId: Long): Flow<Int> =
        habitDao.getTotalCompletions(habitId)
}
