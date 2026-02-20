package com.relyvo.izem

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.ads.MobileAds
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.relyvo.izem.service.ReminderWorker
import com.relyvo.izem.utils.InterstitialAdManager
import java.util.concurrent.TimeUnit

class IzemApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(PersistentCacheSettings.newBuilder().build())
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings

        MobileAds.initialize(this) {}
        InterstitialAdManager.loadInterstitial(this)

        createNotificationChannel()

        scheduleReminder()

        com.google.firebase.messaging.FirebaseMessaging.getInstance().subscribeToTopic("all_users")
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
        val workRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .addTag("izem_reminder")
            .build()

        WorkManager.getInstance(this).enqueueUniqueWork(
            "izem_reminder",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
}