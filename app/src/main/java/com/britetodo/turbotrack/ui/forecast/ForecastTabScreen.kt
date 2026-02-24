package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.theme.TurboNavy

@Composable
fun ForecastTabScreen(
    modifier: Modifier = Modifier,
    viewModel: RouteViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    AnimatedContent(
        targetState = state.currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        modifier = modifier
            .fillMaxSize()
            .background(TurboNavy),
        label = "forecastScreen"
    ) { screen ->
        when (screen) {
            ForecastScreen.Input -> {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                ) {
                    RouteInputScreen(viewModel = viewModel)
                }
            }
            ForecastScreen.Analysis -> ForecastAnalysisScreen(viewModel = viewModel)
            ForecastScreen.Story -> ForecastStoryScreen(viewModel = viewModel)
            ForecastScreen.Result -> ForecastResultScreen(viewModel = viewModel)
        }
    }
}
