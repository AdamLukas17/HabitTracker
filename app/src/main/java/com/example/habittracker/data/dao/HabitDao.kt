package com.example.habittracker.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitCompletion
import com.example.habittracker.data.model.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Transaction
    @Query("SELECT * FROM habits ORDER BY createdAt DESC")
    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>>

    @Transaction
    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitWithCompletions(habitId: Long): Flow<HabitWithCompletions?>

    @Query("SELECT * FROM habits WHERE id = :habitId")
    suspend fun getHabitById(habitId: Long): Habit?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: Habit): Long

    @Update
    suspend fun updateHabit(habit: Habit)

    @Delete
    suspend fun deleteHabit(habit: Habit)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCompletion(completion: HabitCompletion)

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND dateMillis = :dateMillis")
    suspend fun deleteCompletion(habitId: Long, dateMillis: Long)

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId AND dateMillis = :dateMillis")
    suspend fun isCompletedOn(habitId: Long, dateMillis: Long): Int

    @Query("SELECT COUNT(*) FROM habit_completions WHERE habitId = :habitId")
    fun getTotalCompletions(habitId: Long): Flow<Int>
}
