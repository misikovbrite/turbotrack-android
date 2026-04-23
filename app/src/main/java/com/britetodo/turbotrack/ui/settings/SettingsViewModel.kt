package com.britetodo.turbotrack.ui.settings

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.billingclient.api.ProductDetails
import com.britetodo.turbotrack.data.preferences.UserPreferences
import com.britetodo.turbotrack.data.preferences.UserPreferencesRepository
import com.britetodo.turbotrack.services.AnalyticsService
import com.britetodo.turbotrack.services.BillingService
import com.britetodo.turbotrack.services.NotificationService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefsRepo: UserPreferencesRepository,
    private val notificationService: NotificationService,
    private val billingService: BillingService,
    val analytics: AnalyticsService
) : ViewModel() {

    val prefs: StateFlow<UserPreferences?> = prefsRepo.userPreferences
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val isPremium: StateFlow<Boolean> = billingService.isPremium
    val hasSuperPro: StateFlow<Boolean> = billingService.hasSuperPro
    val showUpsell: StateFlow<Boolean> = billingService.showUpsell
    val showPaywall: StateFlow<Boolean> = billingService.showPaywall
    val paywallSource: StateFlow<String> = billingService.paywallSource
    val products: StateFlow<Map<String, ProductDetails>> = billingService.products

    fun subscribe(activity: Activity, productId: String) {
        billingService.launchPurchaseFlow(activity, productId)
    }

    fun restorePurchases() {
        billingService.queryExistingPurchases()
    }

    fun dismissUpsell() {
        billingService.dismissUpsell()
    }

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

    fun showPaywall(source: String) {
        billingService.showPaywall(source)
    }

    fun triggerUpsell() {
        billingService.triggerUpsell()
    }

    fun dismissPaywall() {
        billingService.dismissPaywall()
    }

    fun debugSetPremium(active: Boolean) {
        billingService.debugSetPremium(active)
    }

    fun debugSetSuperPro(active: Boolean) {
        billingService.debugSetSuperPro(active)
    }

    fun debugShowUpsell() {
        billingService.debugShowUpsell()
    }

    fun debugReset() {
        billingService.debugReset()
    }

    fun debugRestartOnboarding() = viewModelScope.launch {
        prefsRepo.setOnboardingCompleted(false)
    }
}
