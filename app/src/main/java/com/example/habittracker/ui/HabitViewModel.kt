package com.example.habittracker.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.habittracker.data.model.Habit
import com.example.habittracker.data.model.HabitWithCompletions
import com.example.habittracker.data.repository.HabitRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class HabitViewModel(private val repository: HabitRepository) : ViewModel() {

    val habitsWithCompletions: StateFlow<List<HabitWithCompletions>> =
        repository.getAllHabitsWithCompletions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedHabit = MutableStateFlow<HabitWithCompletions?>(null)
    val selectedHabit: StateFlow<HabitWithCompletions?> = _selectedHabit.asStateFlow()

    fun selectHabit(habitId: Long) {
        viewModelScope.launch {
            repository.getHabitWithCompletions(habitId)
                .collect { _selectedHabit.value = it }
        }
    }

    fun addHabit(habit: Habit) {
        viewModelScope.launch {
            repository.insertHabit(habit)
        }
    }

    fun updateHabit(habit: Habit) {
        viewModelScope.launch {
            repository.updateHabit(habit)
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            repository.deleteHabit(habit)
        }
    }

    fun toggleCompletion(habitId: Long, date: LocalDate = LocalDate.now()) {
        viewModelScope.launch {
            val millis = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            repository.toggleCompletion(habitId, millis)
        }
    }

    companion object {
        fun factory(repository: HabitRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HabitViewModel(repository) as T
                }
            }
    }
}

fun todayMillis(): Long =
    LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun dateToMillis(date: LocalDate): Long =
    date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

fun millisToDate(millis: Long): LocalDate =
    java.time.Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
