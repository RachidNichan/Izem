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
    private val mLoadingAds = mutableSetOf<String>()
    private const val TAG = "IzemAd"

    const val AD_UNIT_QUIZ = "ca-app-pub-4765019619595524/3728609903"
    const val AD_UNIT_WORD_LIST_BACK = "ca-app-pub-4765019619595524/2776111229"
    const val AD_UNIT_LESSON_END = "ca-app-pub-4765019619595524/9537422082"

    fun loadInterstitial(context: Context, adUnitId: String = AD_UNIT_QUIZ) {
        if (mInterstitialAds[adUnitId] != null) {
            Log.d(TAG, "Ad already loaded for unit: $adUnitId")
            return
        }
        if (mLoadingAds.contains(adUnitId)) {
            Log.d(TAG, "Ad is already loading for unit: $adUnitId")
            return
        }
        mLoadingAds.add(adUnitId)
        val adRequest = AdRequest.Builder().build()
        Log.d(TAG, "Loading interstitial ad for unit: $adUnitId")

        InterstitialAd.load(context, adUnitId, adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.e(TAG, "Ad failed to load ($adUnitId): ${adError.message} (Code: ${adError.code})")
                mInterstitialAds[adUnitId] = null
                mLoadingAds.remove(adUnitId)
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad loaded successfully for unit: $adUnitId")
                mInterstitialAds[adUnitId] = interstitialAd
                mLoadingAds.remove(adUnitId)
            }
        })
    }

    fun showInterstitial(activity: Activity, adUnitId: String = AD_UNIT_QUIZ, onAdDismissed: () -> Unit) {
        val ad = mInterstitialAds[adUnitId]
        if (ad != null) {
            Log.d(TAG, "Showing interstitial ad for unit: $adUnitId")
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d(TAG, "Ad dismissed ($adUnitId).")
                    mInterstitialAds[adUnitId] = null
                    loadInterstitial(activity, adUnitId)
                    onAdDismissed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    Log.e(TAG, "Ad failed to show ($adUnitId): ${adError.message}")
                    mInterstitialAds[adUnitId] = null
                    loadInterstitial(activity, adUnitId)
                    onAdDismissed()
                }
                
                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "Ad showed full screen content ($adUnitId).")
                    mInterstitialAds[adUnitId] = null
                }
            }
            ad.show(activity)
        } else {
            Log.d(TAG, "Ad was not ready yet ($adUnitId). Proceeding...")
            onAdDismissed()
            loadInterstitial(activity, adUnitId)
        }
    }
}
