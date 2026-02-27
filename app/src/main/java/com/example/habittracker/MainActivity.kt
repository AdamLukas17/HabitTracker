package com.example.habittracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.habittracker.ui.HabitViewModel
import com.example.habittracker.ui.navigation.HabitNavGraph
import com.example.habittracker.ui.theme.HabitTrackerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as HabitTrackerApp

        setContent {
            HabitTrackerTheme {
                val navController = rememberNavController()
                val viewModel: HabitViewModel = viewModel(
                    factory = HabitViewModel.factory(app.repository)
                )
                HabitNavGraph(
                    navController = navController,
                    viewModel = viewModel,
                )
            }
        }
    }
}
