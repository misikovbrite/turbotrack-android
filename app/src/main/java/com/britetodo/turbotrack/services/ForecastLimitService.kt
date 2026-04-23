package com.britetodo.turbotrack.services

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ForecastLimitService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("forecast_limit_prefs", Context.MODE_PRIVATE)

    private fun todayKey() = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())

    fun canCheck(): Boolean {
        val today = todayKey()
        val savedDate = prefs.getString(KEY_DATE, "")
        if (savedDate != today) return true
        return prefs.getInt(KEY_COUNT, 0) < FREE_DAILY_LIMIT
    }

    fun recordCheck() {
        val today = todayKey()
        val savedDate = prefs.getString(KEY_DATE, "")
        val count = if (savedDate == today) prefs.getInt(KEY_COUNT, 0) else 0
        prefs.edit()
            .putString(KEY_DATE, today)
            .putInt(KEY_COUNT, count + 1)
            .apply()
    }

    companion object {
        const val FREE_DAILY_LIMIT = 2
        private const val KEY_DATE = "limit_date"
        private const val KEY_COUNT = "limit_count"
    }
}
