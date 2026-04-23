package com.britetodo.turbotrack.ui.forecast

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.theme.TextSecondary
import com.britetodo.turbotrack.ui.components.SuperProBanner
import com.britetodo.turbotrack.ui.settings.SettingsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForecastTabScreen(
    modifier: Modifier = Modifier,
    onOpenSettings: () -> Unit = {},
    viewModel: RouteViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val isPremium by settingsViewModel.isPremium.collectAsState()
    val hasSuperPro by settingsViewModel.hasSuperPro.collectAsState()

    val isInputScreen = state.currentScreen == ForecastScreen.Input

    Scaffold(
        modifier = modifier,
        containerColor = TurboBackground,
        topBar = {
            if (isInputScreen) {
                TopAppBar(
                    title = {
                        Text(
                            text = "Turbulence Forecast",
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                    },
                    actions = {
                        if (!isPremium) {
                            IconButton(onClick = { settingsViewModel.showPaywall("forecast_toolbar") }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Upgrade",
                                    tint = Color(0xFFFF9500)
                                )
                            }
                        }
                        IconButton(onClick = onOpenSettings) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = TextSecondary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = TurboCard,
                        scrolledContainerColor = TurboCard
                    )
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(TurboBackground)
                .padding(innerPadding)
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

            // Super Pro banner at bottom, only when premium but not super pro and on input screen
            if (isPremium && !hasSuperPro && isInputScreen) {
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
}
