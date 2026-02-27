package com.example.habittracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.habittracker.ui.HabitViewModel
import com.example.habittracker.ui.screens.AddEditHabitScreen
import com.example.habittracker.ui.screens.HabitDetailScreen
import com.example.habittracker.ui.screens.HomeScreen

object Routes {
    const val HOME = "home"
    const val ADD_HABIT = "add_habit"
    const val EDIT_HABIT = "edit_habit/{habitId}"
    const val HABIT_DETAIL = "habit_detail/{habitId}"

    fun editHabit(habitId: Long) = "edit_habit/$habitId"
    fun habitDetail(habitId: Long) = "habit_detail/$habitId"
}

@Composable
fun HabitNavGraph(
    navController: NavHostController,
    viewModel: HabitViewModel,
) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onAddHabit = { navController.navigate(Routes.ADD_HABIT) },
                onHabitClick = { habitId -> navController.navigate(Routes.habitDetail(habitId)) },
            )
        }

        composable(Routes.ADD_HABIT) {
            AddEditHabitScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.EDIT_HABIT,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            AddEditHabitScreen(
                viewModel = viewModel,
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
            )
        }

        composable(
            route = Routes.HABIT_DETAIL,
            arguments = listOf(navArgument("habitId") { type = NavType.LongType }),
        ) { backStackEntry ->
            val habitId = backStackEntry.arguments?.getLong("habitId") ?: return@composable
            HabitDetailScreen(
                viewModel = viewModel,
                habitId = habitId,
                onNavigateBack = { navController.popBackStack() },
                onEditHabit = { id ->
                    navController.navigate(Routes.editHabit(id))
                },
            )
        }
    }
}
