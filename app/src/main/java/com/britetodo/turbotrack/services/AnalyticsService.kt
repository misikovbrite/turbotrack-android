package com.britetodo.turbotrack.services

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsService @Inject constructor(
    @ApplicationContext context: Context
) {
    private val analytics = FirebaseAnalytics.getInstance(context)

    fun logTutorialComplete() {
        analytics.logEvent(FirebaseAnalytics.Event.TUTORIAL_COMPLETE, null)
    }

    fun logForecastGenerated(originIata: String, destinationIata: String, severity: String) {
        val params = Bundle().apply {
            putString("origin", originIata)
            putString("destination", destinationIata)
            putString("severity", severity)
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "turbulence_forecast")
        }
        analytics.logEvent("forecast_generated", params)
    }

    fun logShareForecast(originIata: String, destinationIata: String) {
        val params = Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, "intent")
            putString(FirebaseAnalytics.Param.CONTENT_TYPE, "turbulence_forecast")
            putString(FirebaseAnalytics.Param.ITEM_ID, "${originIata}_${destinationIata}")
        }
        analytics.logEvent(FirebaseAnalytics.Event.SHARE, params)
    }

    fun logPurchase(productId: String, price: Double, currency: String = "USD") {
        val item = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productId)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "subscription")
        }
        val params = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, price)
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
        }
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, params)
    }
}
