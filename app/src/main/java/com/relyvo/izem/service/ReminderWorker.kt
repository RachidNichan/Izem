package com.relyvo.izem.service

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.relyvo.izem.R
import com.relyvo.izem.data.SettingsRepo
import kotlinx.coroutines.flow.first

class ReminderWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val settingsRepo = SettingsRepo(applicationContext)
        val isArabic = settingsRepo.isArabic.first()

        val title = if (isArabic) "الأسد في انتظارك! 🦁" else "The Lion is Waiting! 🦁"
        val message = if (isArabic)
            "مرت 24 ساعة. هل أنت مستعد لتعلم كلمات تمازيغت جديدة اليوم؟"
        else "It's been 24 hours. Ready to learn new Tamazight words today?"

        showNotification(title, message)

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "izem_notifications"

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.mipmap.ic_launcher_round)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1002, notification)
    }
}