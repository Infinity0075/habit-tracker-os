package com.anant.disciplinecore.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.anant.disciplinecore.R

object NotificationHelper {

    private const val CHANNEL_ID = "discipline_core_channel"
    private const val CHANNEL_NAME = "DisciplineCore"
    private const val CHANNEL_DESCRIPTION =
        "Daily discipline reminders and focus notifications"

    // =========================================================
    // CREATE CHANNEL
    // =========================================================

    fun createNotificationChannel(
        context: Context
    ) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {

                description = CHANNEL_DESCRIPTION
            }

            val manager =
                context.getSystemService(
                    NotificationManager::class.java
                )

            manager.createNotificationChannel(channel)
        }
    }

    // =========================================================
    // SHOW DAILY REMINDER
    // =========================================================

    fun showDailyReminder(
        context: Context,
        title: String = "Discipline Check",
        message: String =
            "Small actions repeated daily create powerful results."
    ) {

        val notification = NotificationCompat.Builder(
            context,
            CHANNEL_ID
        )
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager =
            context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        manager.notify(
            System.currentTimeMillis().toInt(),
            notification
        )
    }

    // =========================================================
    // SHOW FOCUS REMINDER
    // =========================================================

    fun showFocusReminder(
        context: Context
    ) {

        showDailyReminder(
            context,
            "Focus Session",
            "Protect your attention. Deep work builds greatness."
        )
    }

    // =========================================================
    // SHOW STREAK REMINDER
    // =========================================================

    fun showStreakReminder(
        context: Context,
        streak: Int
    ) {

        showDailyReminder(
            context,
            "🔥 $streak Day Streak",
            "Your consistency is becoming your identity."
        )
    }
}