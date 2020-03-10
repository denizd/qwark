package com.denizd.qwark.util

import android.annotation.TargetApi
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.denizd.qwark.R
import com.denizd.qwark.activity.MainActivity

private const val notificationChannelId = "qwark_exam_notification"

class ExamService : IntentService("ExamService") {

    override fun onHandleIntent(intent: Intent?) {
        sendNotification(course = intent?.getStringExtra("course") ?: "error")
    }

    private fun sendNotification(course: String) {
        val openAppPending = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
            }, 0
        )

        val notificationService = this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(this, notificationService)
        }

        val notification = NotificationCompat.Builder(this, notificationChannelId)
//            .setStyle(NotificationCompat.BigTextStyle())
            .setContentTitle(this.getString(R.string.exam_reminder, course))
            .setContentText(this.getString(R.string.writing_an_exam_today, course))
            .setContentIntent(openAppPending)
            .setAutoCancel(true)
            .setColor(this.getColor(R.color.colorAccent))
            .setSmallIcon(R.drawable.bolt)
            .build()

        notificationService.notify(42, notification)
    }

    @TargetApi(26)
    private fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {
        if (!Dependencies.repo.prefs.getIsNotificationChannelCreated()) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                notificationChannelId, context.getString(R.string.exams), NotificationManager.IMPORTANCE_DEFAULT
            ).also { channel ->
                channel.enableLights(true)
                channel.lightColor = Color.GREEN
            })
            Dependencies.repo.prefs.setIsNotificationChannelCreated(true)
        }
    }
}