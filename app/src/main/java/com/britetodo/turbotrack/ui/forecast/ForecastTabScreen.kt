package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.ui.components.SuperProBanner
import com.britetodo.turbotrack.ui.settings.SettingsViewModel

@Composable
fun ForecastTabScreen(
    modifier: Modifier = Modifier,
    viewModel: RouteViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isPremium by settingsViewModel.isPremium.collectAsState()
    val hasSuperPro by settingsViewModel.hasSuperPro.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(TurboBackground)
    ) {
        AnimatedContent(
            targetState = state.currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.fillMaxSize(),
            label = "forecastScreen"
        ) { screen ->
            when (screen) {
                ForecastScreen.Input -> RouteInputScreen(viewModel = viewModel)
                ForecastScreen.Analysis -> ForecastAnalysisScreen(viewModel = viewModel)
                ForecastScreen.Story -> ForecastStoryScreen(viewModel = viewModel)
                ForecastScreen.Result -> ForecastResultScreen(viewModel = viewModel)
            }
        }

        // Super Pro banner at bottom, only when premium but not super pro
        if (isPremium && !hasSuperPro && state.currentScreen == ForecastScreen.Input) {
            SuperProBanner(
                source = "forecast",
                onTap = { source ->
                    settingsViewModel.analytics.logUpsellBannerClicked(source)
                    settingsViewModel.triggerUpsell()
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp)
            )
        }
    }
}
