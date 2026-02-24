package com.britetodo.turbotrack.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "turbotrack_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
        val QUIZ_Q1 = stringPreferencesKey("quiz_q1")
        val QUIZ_Q2 = stringPreferencesKey("quiz_q2")
        val QUIZ_Q3 = stringPreferencesKey("quiz_q3")
        val QUIZ_Q4 = stringSetPreferencesKey("quiz_q4")
        val QUIZ_Q5 = stringPreferencesKey("quiz_q5")
        val UNITS_FEET = booleanPreferencesKey("units_feet")
        val NOTIFICATIONS_ENABLED = booleanPreferencesKey("notifications_enabled")
        val NOTIFICATION_TIMING = intPreferencesKey("notification_timing")
        val DATA_REFRESH_ENABLED = booleanPreferencesKey("data_refresh_enabled")
        val DATA_REFRESH_INTERVAL = intPreferencesKey("data_refresh_interval")
    }

    val userPreferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            onboardingCompleted = prefs[Keys.ONBOARDING_COMPLETED] ?: false,
            quizQ1Answer = prefs[Keys.QUIZ_Q1] ?: "",
            quizQ2Answer = prefs[Keys.QUIZ_Q2] ?: "",
            quizQ3Answer = prefs[Keys.QUIZ_Q3] ?: "",
            quizQ4Answers = prefs[Keys.QUIZ_Q4] ?: emptySet(),
            quizQ5Answer = prefs[Keys.QUIZ_Q5] ?: "",
            unitsFeet = prefs[Keys.UNITS_FEET] ?: true,
            notificationsEnabled = prefs[Keys.NOTIFICATIONS_ENABLED] ?: false,
            notificationTiming = prefs[Keys.NOTIFICATION_TIMING] ?: 24,
            dataRefreshEnabled = prefs[Keys.DATA_REFRESH_ENABLED] ?: true,
            dataRefreshInterval = prefs[Keys.DATA_REFRESH_INTERVAL] ?: 5
        )
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[Keys.ONBOARDING_COMPLETED] = completed
        }
    }

    suspend fun saveQuizAnswers(
        q1: String = "",
        q2: String = "",
        q3: String = "",
        q4: Set<String> = emptySet(),
        q5: String = ""
    ) {
        context.dataStore.edit { prefs ->
            if (q1.isNotEmpty()) prefs[Keys.QUIZ_Q1] = q1
            if (q2.isNotEmpty()) prefs[Keys.QUIZ_Q2] = q2
            if (q3.isNotEmpty()) prefs[Keys.QUIZ_Q3] = q3
            if (q4.isNotEmpty()) prefs[Keys.QUIZ_Q4] = q4
            if (q5.isNotEmpty()) prefs[Keys.QUIZ_Q5] = q5
        }
    }

    suspend fun setUnitsFeet(feet: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.UNITS_FEET] = feet }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.NOTIFICATIONS_ENABLED] = enabled }
    }

    suspend fun setNotificationTiming(hours: Int) {
        context.dataStore.edit { prefs -> prefs[Keys.NOTIFICATION_TIMING] = hours }
    }

    suspend fun setDataRefreshEnabled(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[Keys.DATA_REFRESH_ENABLED] = enabled }
    }

    suspend fun setDataRefreshInterval(minutes: Int) {
        context.dataStore.edit { prefs -> prefs[Keys.DATA_REFRESH_INTERVAL] = minutes }
    }
}
