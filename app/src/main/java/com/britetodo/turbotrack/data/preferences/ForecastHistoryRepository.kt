package com.britetodo.turbotrack.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

data class HistoryEntry(
    val originIata: String,
    val originCity: String,
    val destIata: String,
    val destCity: String,
    val severity: String,
    val dateFormatted: String
)

@Singleton
class ForecastHistoryRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("forecast_history", Context.MODE_PRIVATE)
    private val gson = Gson()

    private val KEY_HISTORY = "history_entries"
    private val MAX_ENTRIES = 5

    fun saveEntry(entry: HistoryEntry) {
        val current = getHistory().toMutableList()
        // Remove duplicate routes
        current.removeAll { it.originIata == entry.originIata && it.destIata == entry.destIata }
        current.add(0, entry)
        val trimmed = current.take(MAX_ENTRIES)
        prefs.edit().putString(KEY_HISTORY, gson.toJson(trimmed)).apply()
    }

    fun getHistory(): List<HistoryEntry> {
        val json = prefs.getString(KEY_HISTORY, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<HistoryEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun clear() {
        prefs.edit().remove(KEY_HISTORY).apply()
    }
}
