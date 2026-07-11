package com.relyvo.izem

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.relyvo.izem.service.ReminderWorker
import com.relyvo.izem.utils.InterstitialAdManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class IzemApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        MobileAds.initialize(this) {
            InterstitialAdManager.loadInterstitial(this, InterstitialAdManager.AD_UNIT_QUIZ)
            InterstitialAdManager.loadInterstitial(this, InterstitialAdManager.AD_UNIT_WORD_LIST_BACK)
        }

        createNotificationChannel()

        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all_users")

        syncFcmToken()
    }

    private fun syncFcmToken() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            com.google.firebase.messaging.FirebaseMessaging.getInstance().token
                .addOnCompleteListener { task ->
                    if (task.isSuccessful && task.result != null) {
                        val newDynamicToken = task.result

                        val sharedPrefs = applicationContext.getSharedPreferences("IzemPrefs", Context.MODE_PRIVATE)
                        val oldToken = sharedPrefs.getString("fcm_token", null)

                        if (newDynamicToken != oldToken) {
                            val repo = com.relyvo.izem.data.FirestoreRepo()
                            repo.updateFcmToken(currentUser.uid, newDynamicToken)

                            sharedPrefs.edit().putString("fcm_token", newDynamicToken).apply()
                            // android.util.Log.d("IzemFCM", "Token updated via repo and saved to prefs! 🦁")
                        } else {
                            android.util.Log.d("IzemFCM", "Token is already synced. Skipping network call. 😎")
                        }
                    } else {
                        android.util.Log.e("IzemFCM", "Fetching FCM registration token failed", task.exception)
                    }
                }
        } else {
            android.util.Log.d("IzemFCM", "No user logged in yet. Token sync skipped.")
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "izem_notifications"
            val name = "Izem Updates & Reminders"
            val descriptionText = "Get notified when your words are approved or new content is added."

            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder() {
        val delay = 24L
        val unit = TimeUnit.HOURS

        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, unit)
            .addTag("izem_reminder")
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "izem_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}