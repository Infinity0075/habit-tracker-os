package com.anant.disciplinecore.core.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.anant.disciplinecore.worker.MidnightResetReceiver
import java.util.Calendar

object ReminderScheduler {

    private const val REQUEST_CODE = 1001

    // =========================================================
    // SCHEDULE DAILY REMINDER
    // =========================================================

    fun scheduleDailyReminder(
        context: Context,
        hour: Int,
        minute: Int
    ) {

        val calendar = Calendar.getInstance().apply {

            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }
        }

        val intent = Intent(
            context,
            MidnightResetReceiver::class.java
        ).apply {
            action = "DAILY_REMINDER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // =========================================================
    // CANCEL DAILY REMINDER
    // =========================================================

    fun cancelDailyReminder(
        context: Context
    ) {

        val intent = Intent(
            context,
            MidnightResetReceiver::class.java
        ).apply {
            action = "DAILY_REMINDER"
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager =
            context.getSystemService(
                Context.ALARM_SERVICE
            ) as AlarmManager

        alarmManager.cancel(pendingIntent)
    }
}