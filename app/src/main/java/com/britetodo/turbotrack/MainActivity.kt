package com.britetodo.turbotrack

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.britetodo.turbotrack.data.preferences.UserPreferencesRepository
import com.britetodo.turbotrack.theme.TurboTrackTheme
import com.britetodo.turbotrack.ui.AppNavigation
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var prefsRepo: UserPreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val prefs by prefsRepo.userPreferences.collectAsState(initial = null)

            // Keep splash until prefs loaded
            splashScreen.setKeepOnScreenCondition { prefs == null }

            TurboTrackTheme {
                prefs?.let { userPrefs ->
                    AppNavigation(onboardingCompleted = userPrefs.onboardingCompleted)
                }
            }
        }
    }
}
