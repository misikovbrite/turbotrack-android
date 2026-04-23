package com.britetodo.turbotrack.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.ui.main.MainScreen
import com.britetodo.turbotrack.ui.onboarding.OnboardingScreen
import com.britetodo.turbotrack.ui.paywall.PaywallScreen
import com.britetodo.turbotrack.ui.settings.SettingsViewModel

@Composable
fun AppNavigation(onboardingCompleted: Boolean) {
    val settingsViewModel: SettingsViewModel = hiltViewModel()
    val isPremium by settingsViewModel.isPremium.collectAsState()

    var onboardingDone by remember { mutableStateOf(onboardingCompleted) }
    var showPaywall by remember { mutableStateOf(false) }
    var paywallDismissedThisSession by remember { mutableStateOf(false) }

    val shouldShowPaywall = !isPremium && !paywallDismissedThisSession

    when {
        !onboardingDone -> OnboardingScreen(
            onComplete = {
                onboardingDone = true
                showPaywall = true
            }
        )
        shouldShowPaywall -> PaywallScreen(
            source = if (showPaywall) "onboarding" else "app_launch",
            onDismiss = {
                showPaywall = false
                paywallDismissedThisSession = true
            },
            viewModel = settingsViewModel
        )
        else -> MainScreen(settingsViewModel = settingsViewModel)
    }
}
