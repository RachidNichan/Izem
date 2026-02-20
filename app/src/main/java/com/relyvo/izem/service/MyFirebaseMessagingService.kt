package com.relyvo.izem.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.relyvo.izem.MainActivity
import com.relyvo.izem.R
import com.relyvo.izem.data.FirestoreRepo // 🔹 استيراد الـ Repo

class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val repo = FirestoreRepo()

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        remoteMessage.notification?.let {
            showNotification(it.title ?: "Izem", it.body ?: "Time to learn!")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Log.d("IzemFCM", "New Token Generated: $token")

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            repo.updateFcmToken(userId, token)
        }
    }

    private fun showNotification(title: String, messageBody: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "izem_notifications"

        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setColor(android.graphics.Color.parseColor("#FF8F00"))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }
}