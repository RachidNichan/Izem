package com.relyvo.izem.service

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.relyvo.izem.MainActivity
import com.relyvo.izem.R
import com.relyvo.izem.data.FirestoreRepo

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            FirestoreRepo().updateFcmToken(currentUser.uid, token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "Izem"
        val body = remoteMessage.notification?.body ?: remoteMessage.data["body"] ?: "Time to learn!"
        
        showNotification(title, body, remoteMessage.data)
    }

    private fun showNotification(title: String, messageBody: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = "LEADERBOARD_ACTION"
            
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
            
            putExtra("navigate_to", "leaderboard")
            putExtra("type", "roar")
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 123, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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