package com.relyvo.izem.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

object InterstitialAdManager {
    private val mInterstitialAds = mutableMapOf<String, InterstitialAd?>()
    private const val TAG = "IzemAd"

    const val AD_UNIT_QUIZ = "ca-app-pub-4765019619595524/3728609903"
    const val AD_UNIT_WORD_LIST_BACK = "ca-app-pub-4765019619595524/2776111229"

    fun loadInterstitial(context: Context, adUnitId: String = AD_UNIT_QUIZ) {
        val adRequest = AdRequest.Builder().build()
        // Log.d(TAG, "Loading interstitial ad for unit: $adUnitId")

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                // Log.e(TAG, "Ad failed to load ($adUnitId): ${adError.message} (Code: ${adError.code})")
                mInterstitialAds[adUnitId] = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                // Log.d(TAG, "Ad loaded successfully for unit: $adUnitId")
                mInterstitialAds[adUnitId] = interstitialAd
            }
        })
    }

    fun showInterstitial(activity: Activity, adUnitId: String = AD_UNIT_QUIZ, onAdDismissed: () -> Unit) {
        val ad = mInterstitialAds[adUnitId]
        if (ad != null) {
            // Log.d(TAG, "Showing interstitial ad for unit: $adUnitId")
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    // Log.d(TAG, "Ad dismissed ($adUnitId).")
                    mInterstitialAds[adUnitId] = null
                    loadInterstitial(activity, adUnitId)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Log.e(TAG, "Ad failed to show ($adUnitId): ${adError.message}")
                    mInterstitialAds[adUnitId] = null
                    onAdDismissed()
                }
                
                override fun onAdShowedFullScreenContent() {
                    // Log.d(TAG, "Ad showed full screen content ($adUnitId).")
                    mInterstitialAds[adUnitId] = null
                }
            }
            ad.show(activity)
        } else {
            // Log.d(TAG, "Ad was not ready yet ($adUnitId).")
            onAdDismissed()
            loadInterstitial(activity, adUnitId)
        }
    }
}
