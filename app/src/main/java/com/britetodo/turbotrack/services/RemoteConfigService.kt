package com.britetodo.turbotrack.services

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigService @Inject constructor() {

    private val remoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600 // 1 hour in production
        })
        remoteConfig.setDefaultsAsync(mapOf(
            KEY_CLOSE_BUTTON_DELAY to 3.0
        ))
    }

    suspend fun fetchAndActivate() {
        try {
            remoteConfig.fetchAndActivate().await()
        } catch (e: Exception) {
            // Use defaults on failure
        }
    }

    val closeButtonDelay: Double
        get() = remoteConfig.getDouble(KEY_CLOSE_BUTTON_DELAY)

    companion object {
        const val KEY_CLOSE_BUTTON_DELAY = "turbulence_close_button_delay"
    }
}
