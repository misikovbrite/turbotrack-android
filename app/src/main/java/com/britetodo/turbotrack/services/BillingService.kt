package com.britetodo.turbotrack.services

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BillingService @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analytics: AnalyticsService
) : PurchasesUpdatedListener {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val prefs: SharedPreferences =
        context.getSharedPreferences("billing_prefs", Context.MODE_PRIVATE)

    // Premium = weekly or yearly subscription active
    private val _isPremium = MutableStateFlow(prefs.getBoolean(KEY_PREMIUM, false))
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    // Super Pro = super_pro_monthly active
    private val _hasSuperPro = MutableStateFlow(prefs.getBoolean(KEY_SUPER_PRO, false))
    val hasSuperPro: StateFlow<Boolean> = _hasSuperPro.asStateFlow()

    // Show upsell paywall after regular premium purchase
    private val _showUpsell = MutableStateFlow(false)
    val showUpsell: StateFlow<Boolean> = _showUpsell.asStateFlow()

    // Paywall overlay state
    private val _showPaywall = MutableStateFlow(false)
    val showPaywall: StateFlow<Boolean> = _showPaywall.asStateFlow()

    private val _paywallSource = MutableStateFlow("unknown")
    val paywallSource: StateFlow<String> = _paywallSource.asStateFlow()

    // All loaded ProductDetails keyed by productId
    private val _products = MutableStateFlow<Map<String, ProductDetails>>(emptyMap())
    val products: StateFlow<Map<String, ProductDetails>> = _products.asStateFlow()

    private val billingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .build()

    init {
        connect()
    }

    private fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    queryProductDetails()
                    queryExistingPurchases()
                }
            }

            override fun onBillingServiceDisconnected() {
                connect()
            }
        })
    }

    private fun queryProductDetails() {
        val productList = ALL_PRODUCT_IDS.map { id ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(id)
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        }

        billingClient.queryProductDetailsAsync(
            QueryProductDetailsParams.newBuilder().setProductList(productList).build()
        ) { result, details ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                _products.value = details.associateBy { it.productId }
            }
        }
    }

    fun queryExistingPurchases() {
        billingClient.queryPurchasesAsync(
            QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        ) { result, purchases ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                val activePurchases = purchases.filter {
                    it.purchaseState == Purchase.PurchaseState.PURCHASED
                }
                val productIds = activePurchases.flatMap { it.products }

                val isPremiumActive = productIds.any { it in PREMIUM_PRODUCT_IDS }
                val isSuperProActive = productIds.any { it == PRODUCT_SUPER_PRO }

                updatePremiumState(isPremiumActive)
                updateSuperProState(isSuperProActive)
                activePurchases.forEach { acknowledgePurchaseIfNeeded(it) }
            }
        }
    }

    fun launchPurchaseFlow(activity: Activity, productId: String) {
        val details = _products.value[productId] ?: return
        val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken ?: return

        analytics.logPurchaseStarted(productId)

        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build()
                )
            )
            .build()

        billingClient.launchBillingFlow(activity, params)
    }

    fun showPaywall(source: String) {
        _paywallSource.value = source
        _showPaywall.value = true
    }

    fun dismissPaywall() {
        _showPaywall.value = false
    }

    fun triggerUpsell() {
        _showUpsell.value = true
    }

    fun dismissUpsell() {
        _showUpsell.value = false
    }

    override fun onPurchasesUpdated(result: BillingResult, purchases: List<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { handlePurchase(it) }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                // Log cancel — we don't know the specific productId here, log generic
                analytics.logPurchaseCancelled("unknown")
            }
            else -> {
                analytics.logPurchaseFailed("code=${result.responseCode}: ${result.debugMessage}")
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState != Purchase.PurchaseState.PURCHASED) return

        val purchasedIds = purchase.products
        val wasPremium = _isPremium.value

        if (purchasedIds.any { it in PREMIUM_PRODUCT_IDS }) {
            updatePremiumState(true)
            val price = purchasedIds.firstOrNull()?.let { productPriceUsd(it) } ?: 0.0
            analytics.logPurchase(purchasedIds.first(), price)

            // Show upsell if just became premium and not already super pro
            if (!wasPremium && !_hasSuperPro.value && !prefs.getBoolean(KEY_UPSELL_SHOWN, false)) {
                prefs.edit().putBoolean(KEY_UPSELL_SHOWN, true).apply()
                scope.launch {
                    delay(1500)
                    _showUpsell.value = true
                }
            }
        }

        if (purchasedIds.any { it == PRODUCT_SUPER_PRO }) {
            updateSuperProState(true)
            analytics.logPurchase(PRODUCT_SUPER_PRO, PRICE_SUPER_PRO)
        }

        acknowledgePurchaseIfNeeded(purchase)
    }

    private fun updatePremiumState(active: Boolean) {
        _isPremium.value = active
        prefs.edit().putBoolean(KEY_PREMIUM, active).apply()
    }

    private fun updateSuperProState(active: Boolean) {
        _hasSuperPro.value = active
        prefs.edit().putBoolean(KEY_SUPER_PRO, active).apply()
    }

    private fun acknowledgePurchaseIfNeeded(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
            ) { }
        }
    }

    private fun productPriceUsd(productId: String): Double = when (productId) {
        PRODUCT_WEEKLY -> PRICE_WEEKLY
        PRODUCT_YEARLY -> PRICE_YEARLY
        PRODUCT_SUPER_PRO -> PRICE_SUPER_PRO
        else -> 0.0
    }

    companion object {
        const val PRODUCT_WEEKLY    = "turb_weekly_4.99"
        const val PRODUCT_YEARLY    = "turb_yearly_19.99"
        const val PRODUCT_SUPER_PRO = "turb_super_pro_19.99"

        val PREMIUM_PRODUCT_IDS = setOf(PRODUCT_WEEKLY, PRODUCT_YEARLY)
        val ALL_PRODUCT_IDS = setOf(PRODUCT_WEEKLY, PRODUCT_YEARLY, PRODUCT_SUPER_PRO)

        const val PRICE_WEEKLY    = 4.99
        const val PRICE_YEARLY    = 19.99
        const val PRICE_SUPER_PRO = 19.99

        private const val KEY_PREMIUM      = "is_premium"
        private const val KEY_SUPER_PRO    = "has_super_pro"
        private const val KEY_UPSELL_SHOWN = "upsell_shown"
    }

    fun debugSetPremium(active: Boolean) {
        if (com.britetodo.turbotrack.BuildConfig.DEBUG) {
            updatePremiumState(active)
        }
    }

    fun debugSetSuperPro(active: Boolean) {
        if (com.britetodo.turbotrack.BuildConfig.DEBUG) {
            updateSuperProState(active)
        }
    }

    fun debugShowUpsell() {
        if (com.britetodo.turbotrack.BuildConfig.DEBUG) {
            _showUpsell.value = true
        }
    }

    fun debugReset() {
        if (com.britetodo.turbotrack.BuildConfig.DEBUG) {
            updatePremiumState(false)
            updateSuperProState(false)
            prefs.edit().putBoolean(KEY_UPSELL_SHOWN, false).apply()
            _showUpsell.value = false
            _showPaywall.value = false
        }
    }
}
