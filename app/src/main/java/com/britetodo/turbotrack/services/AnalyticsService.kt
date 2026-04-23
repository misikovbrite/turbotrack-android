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

    fun logPurchase(productId: String, price: Double, transactionId: String = "", currency: String = "USD") {
        val item = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productId)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "subscription")
        }
        val params = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, price)
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
            if (transactionId.isNotEmpty()) {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId)
            }
        }
        analytics.logEvent(FirebaseAnalytics.Event.PURCHASE, params)
    }

    // Fired when user starts a free trial — no revenue yet, value = 0.
    // In Google Ads: use as micro-conversion or secondary goal.
    fun logTrialStarted(productId: String, transactionId: String = "", currency: String = "USD") {
        val item = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productId)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "subscription")
        }
        val params = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, 0.0)
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
            if (transactionId.isNotEmpty()) {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId)
            }
        }
        analytics.logEvent("trial_started", params)
    }

    // Fired when trial converts to paid (detected on next app open after trial period).
    // Set as primary conversion in Google Ads with the subscription value.
    fun logSubscriptionConverted(productId: String, price: Double, transactionId: String = "", currency: String = "USD") {
        val item = Bundle().apply {
            putString(FirebaseAnalytics.Param.ITEM_ID, productId)
            putString(FirebaseAnalytics.Param.ITEM_NAME, productId)
            putString(FirebaseAnalytics.Param.ITEM_CATEGORY, "subscription")
        }
        val params = Bundle().apply {
            putDouble(FirebaseAnalytics.Param.VALUE, price)
            putString(FirebaseAnalytics.Param.CURRENCY, currency)
            putParcelableArray(FirebaseAnalytics.Param.ITEMS, arrayOf(item))
            if (transactionId.isNotEmpty()) {
                putString(FirebaseAnalytics.Param.TRANSACTION_ID, transactionId)
            }
        }
        // Log both as named event (for segmentation) and as standard PURCHASE (for GA4/Ads import)
        analytics.logEvent("subscription_converted", params)
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
