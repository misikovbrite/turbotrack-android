package com.britetodo.turbotrack.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.britetodo.turbotrack.ui.main.MainScreen
import com.britetodo.turbotrack.ui.onboarding.OnboardingScreen

sealed class Screen(val route: String) {
    object Onboarding : Screen("onboarding")
    object Main : Screen("main")
}

@Composable
fun AppNavigation(onboardingCompleted: Boolean) {
    val navController = rememberNavController()
    val startDestination = if (onboardingCompleted) Screen.Main.route else Screen.Onboarding.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onComplete = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Main.route) {
            MainScreen()
        }
    }
}
