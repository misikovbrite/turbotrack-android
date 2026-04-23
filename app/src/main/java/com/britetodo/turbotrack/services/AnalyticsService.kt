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

    fun logPaywallShown(source: String) {
        analytics.logEvent("paywall_shown", Bundle().apply { putString("source", source) })
    }

    fun logPaywallDismissed(source: String) {
        analytics.logEvent("paywall_dismissed", Bundle().apply { putString("source", source) })
    }

    fun logPurchaseStarted(productId: String) {
        analytics.logEvent("purchase_started", Bundle().apply { putString("product_id", productId) })
    }

    fun logPurchaseCancelled(productId: String) {
        analytics.logEvent("purchase_cancelled", Bundle().apply { putString("product_id", productId) })
    }

    fun logPurchaseFailed(error: String) {
        analytics.logEvent("purchase_failed", Bundle().apply { putString("error", error) })
    }

    fun logUpsellViewed() {
        analytics.logEvent("upsell_paywall_viewed", null)
    }

    fun logUpsellCtaClicked() {
        analytics.logEvent("upsell_cta_clicked", null)
    }

    fun logUpsellPurchaseStarted(productId: String) {
        analytics.logEvent("upsell_purchase_started", Bundle().apply { putString("product_id", productId) })
    }

    fun logUpsellClosed() {
        analytics.logEvent("upsell_paywall_closed", null)
    }

    fun logUpsellBannerClicked(source: String) {
        analytics.logEvent("upsell_banner_clicked", Bundle().apply { putString("source", source) })
    }

    fun logRestorePurchases(success: Boolean) {
        analytics.logEvent("restore_purchases", Bundle().apply { putBoolean("success", success) })
    }
}
