package com.example.reminderapp.receivers

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.reminderapp.data.model.ApiNotificationData
import dagger.hilt.android.qualifiers.ApplicationContext
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import android.os.Build

@Singleton
class AlarmScheduler @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    
    fun scheduleNotification(notification: ApiNotificationData) {
        try {
            android.util.Log.d("AlarmScheduler", "Scheduling notification ${notification.id} for ${notification.tarih}")
            
            val triggerTime = parseNotificationTime(notification.tarih)
            if (triggerTime == null) {
                Log.e("AlarmScheduler", "Invalid date format: ${notification.tarih}")
                return
            }
            
            // Geçmiş tarih kontrolü
            if (triggerTime <= System.currentTimeMillis()) {
                Log.w("AlarmScheduler", "Cannot schedule alarm for past time: ${notification.tarih}")
                return
            }
            
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra(AlarmReceiver.EXTRA_NOTIFICATION_ID, notification.id)
                putExtra(AlarmReceiver.EXTRA_TITLE, "Ajanda Hatırlatması")
                putExtra(AlarmReceiver.EXTRA_DESCRIPTION, notification.aciklama ?: "")
                putExtra(AlarmReceiver.EXTRA_FIRMA, notification.firma ?: "")
                putExtra(AlarmReceiver.EXTRA_AD_SOYAD, notification.adSoyad ?: "")
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notification.id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Exact alarm izni kontrolü
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    // Exact alarm kur
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d("AlarmScheduler", "Exact alarm scheduled for notification ${notification.id}")
                } else {
                    // Exact alarm izni yoksa normal alarm kur
                    alarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        triggerTime,
                        pendingIntent
                    )
                    Log.d("AlarmScheduler", "Regular alarm scheduled for notification ${notification.id} (exact permission not available)")
                }
            } else {
                // Android 12 öncesi için normal alarm
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
                Log.d("AlarmScheduler", "Exact alarm scheduled for notification ${notification.id} (Android < 12)")
            }
            
            Log.d("AlarmScheduler", "Alarm scheduled for notification ${notification.id} at ${notification.tarih} (trigger time: $triggerTime)")
            
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error scheduling alarm for notification ${notification.id}", e)
        }
    }
    
    fun cancelNotification(notificationId: Int) {
        try {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.cancel(pendingIntent)
            Log.d("AlarmScheduler", "Alarm cancelled for notification $notificationId")
            
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error cancelling alarm for notification $notificationId", e)
        }
    }
    
    private fun parseNotificationTime(dateTimeString: String?): Long? {
        if (dateTimeString.isNullOrBlank()) return null
        
        return try {
            // ISO 8601 formatını parse et
            val cleaned = dateTimeString.replace("Z", "")
            val localDateTime = LocalDateTime.parse(cleaned.substring(0, 19))
            val zonedDateTime = localDateTime.atZone(ZoneId.systemDefault())
            zonedDateTime.toInstant().toEpochMilli()
        } catch (e: Exception) {
            Log.e("AlarmScheduler", "Error parsing date: $dateTimeString", e)
            null
        }
    }
}
