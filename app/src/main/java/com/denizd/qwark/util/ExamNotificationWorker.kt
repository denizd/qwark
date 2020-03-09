package com.denizd.qwark.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.denizd.qwark.R
import com.denizd.qwark.activity.MainActivity

private const val notificationChannelId = "qwark_exam_notification"

class ExamNotificationWorker(private val context: Context, workerParams: WorkerParameters) : Worker(context.applicationContext, workerParams) {

    override fun doWork(): Result {
        sendNotification(course = inputData.getString("course") ?: "error")
        return Result.success()
    }

    // TODO launch fragment from intent and launch the fragment for the course
    private fun sendNotification(course: String) {
        val openAppPending = PendingIntent.getActivity(
            context, 0,
            Intent(context, MainActivity::class.java).also { intent ->
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK + Intent.FLAG_ACTIVITY_CLEAR_TASK
            }, 0
        )

        val notificationService = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, notificationService)
        }

        val notification = NotificationCompat.Builder(context, notificationChannelId)
//            .setStyle(NotificationCompat.BigTextStyle())
            .setContentTitle(context.getString(R.string.exam_reminder, course))
            .setContentText(context.getString(R.string.writing_an_exam_today, course))
            .setContentIntent(openAppPending)
            .setAutoCancel(true)
            .setColor(context.getColor(R.color.colorAccent))
            .setSmallIcon(R.drawable.bolt)
            .build()

        notificationService.notify(42, notification)
    }

    @TargetApi(26)
    private fun createNotificationChannel(context: Context, notificationManager: NotificationManager) {
        if (!Dependencies.repo.prefs.getIsNotificationChannelCreated()) {
            notificationManager.createNotificationChannel(NotificationChannel(
                notificationChannelId, context.getString(R.string.exams), NotificationManager.IMPORTANCE_DEFAULT
            ).also { channel ->
                channel.enableLights(true)
                channel.lightColor = Color.GREEN
            })
            Dependencies.repo.prefs.setIsNotificationChannelCreated(true)
        }
    }
}