package com.britetodo.turbotrack.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.britetodo.turbotrack.data.preferences.UserPreferences
import com.britetodo.turbotrack.data.preferences.UserPreferencesRepository
import com.britetodo.turbotrack.services.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository,
    private val notificationService: NotificationService
) : ViewModel() {

    val prefs: StateFlow<UserPreferences?> = prefsRepo.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun setNotificationsEnabled(enabled: Boolean) = viewModelScope.launch {
        prefsRepo.setNotificationsEnabled(enabled)
        if (enabled) {
            notificationService.scheduleReminder(prefs.value?.notificationTiming ?: 24)
        } else {
            notificationService.cancelReminder()
        }
    }

    fun setNotificationTiming(hours: Int) = viewModelScope.launch {
        prefsRepo.setNotificationTiming(hours)
        if (prefs.value?.notificationsEnabled == true) {
            notificationService.scheduleReminder(hours)
        }
    }

    fun setUnitsFeet(feet: Boolean) = viewModelScope.launch {
        prefsRepo.setUnitsFeet(feet)
    }

    fun setDataRefreshEnabled(enabled: Boolean) = viewModelScope.launch {
        prefsRepo.setDataRefreshEnabled(enabled)
    }

    fun setDataRefreshInterval(minutes: Int) = viewModelScope.launch {
        prefsRepo.setDataRefreshInterval(minutes)
    }
}
