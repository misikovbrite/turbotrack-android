package com.britetodo.turbotrack.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.britetodo.turbotrack.theme.TurboBackground
import com.britetodo.turbotrack.theme.TurboBlue
import com.britetodo.turbotrack.theme.TurboCard
import com.britetodo.turbotrack.theme.TextMuted
import com.britetodo.turbotrack.theme.TextPrimary
import com.britetodo.turbotrack.ui.forecast.ForecastTabScreen
import com.britetodo.turbotrack.ui.map.TurbulenceMapScreen
import com.britetodo.turbotrack.ui.reports.ReportsScreen
import com.britetodo.turbotrack.ui.settings.SettingsScreen

private data class TabItem(
    val title: String,
    val icon: ImageVector
)

private val tabs = listOf(
    TabItem("Map", Icons.Default.Map),
    TabItem("Forecast", Icons.Default.Flight),
    TabItem("Reports", Icons.Default.List),
    TabItem("Settings", Icons.Default.Settings)
)

@Composable
fun MainScreen() {
    var selectedTab by rememberSaveable { mutableIntStateOf(1) } // Default to Forecast

    Scaffold(
        containerColor = TurboBackground,
        bottomBar = {
            NavigationBar(
                containerColor = TurboCard,
                contentColor = TextPrimary,
                tonalElevation = 0.dp
            ) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        icon = {
                            Icon(imageVector = tab.icon, contentDescription = tab.title)
                        },
                        label = { Text(tab.title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TurboBlue,
                            selectedTextColor = TurboBlue,
                            unselectedIconColor = TextMuted,
                            unselectedTextColor = TextMuted,
                            indicatorColor = TurboBlue.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> TurbulenceMapScreen(modifier = Modifier.padding(innerPadding))
            1 -> ForecastTabScreen(modifier = Modifier.padding(innerPadding))
            2 -> ReportsScreen(modifier = Modifier.padding(innerPadding))
            3 -> SettingsScreen(modifier = Modifier.padding(innerPadding))
        }
    }
}
