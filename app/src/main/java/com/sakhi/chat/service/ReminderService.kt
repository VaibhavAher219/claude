package com.sakhi.chat.service

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sakhi.chat.R
import java.util.*

class ReminderService(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "sakhi_reminders"
        const val CHANNEL_NAME = "सखी आठवणी"
        const val NOTIFICATION_ID_BASE = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "सखी कडून आठवण करणारे सूचना"
                enableVibration(true)
                enableLights(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun scheduleReminder(reminder: Reminder): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("reminder_id", reminder.id)
            putExtra("reminder_title", reminder.title)
            putExtra("reminder_message", reminder.message)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminder.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        reminder.time,
                        pendingIntent
                    )
                    true
                } else {
                    // Request permission
                    false
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    reminder.time,
                    pendingIntent
                )
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun cancelReminder(reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    fun showNotification(reminder: Reminder) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(reminder.title)
            .setContentText(reminder.message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        NotificationManagerCompat.from(context).notify(
            NOTIFICATION_ID_BASE + reminder.id.toInt(),
            notification
        )
    }
}

data class Reminder(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String,
    val time: Long,
    val conversationId: String = "",
    val isRecurring: Boolean = false,
    val recurringPattern: RecurringPattern? = null
)

enum class RecurringPattern {
    DAILY,
    WEEKLY,
    MONTHLY
}

class ReminderBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra("reminder_id", 0)
        val title = intent.getStringExtra("reminder_title") ?: "सखी आठवण"
        val message = intent.getStringExtra("reminder_message") ?: ""

        val reminder = Reminder(
            id = reminderId,
            title = title,
            message = message,
            time = System.currentTimeMillis()
        )

        val reminderService = ReminderService(context)
        reminderService.showNotification(reminder)
    }
}
