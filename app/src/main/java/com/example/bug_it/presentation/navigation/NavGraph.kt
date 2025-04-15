package com.example.bug_it.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.bug_it.presentation.screens.BugSubmissionScreen
import com.example.bug_it.presentation.screens.BugsScreen

sealed class Screen(val route: String) {
    data object BugSubmission : Screen("bug_submission")
    data object Bugs : Screen("bugs")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String = Screen.BugSubmission.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.BugSubmission.route) {
            BugSubmissionScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToBugs = { navController.navigate(Screen.Bugs.route) }
            )
        }

        composable(Screen.Bugs.route) {
            BugsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
} 