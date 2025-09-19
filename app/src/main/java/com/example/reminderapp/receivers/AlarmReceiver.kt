package com.example.reminderapp.receivers

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.reminderapp.MainActivity
import com.example.reminderapp.R

class AlarmReceiver : BroadcastReceiver() {
    
    companion object {
        const val EXTRA_NOTIFICATION_ID = "notification_id"
        const val EXTRA_TITLE = "title"
        const val EXTRA_DESCRIPTION = "description"
        const val EXTRA_FIRMA = "firma"
        const val EXTRA_AD_SOYAD = "ad_soyad"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        // Bildirim izni kontrolü
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                android.util.Log.w("AlarmReceiver", "Notification permission not granted")
                return
            }
        }
        
        val notificationId = intent.getIntExtra(EXTRA_NOTIFICATION_ID, 0)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Ajanda Hatırlatması"
        val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: ""
        val firma = intent.getStringExtra(EXTRA_FIRMA) ?: ""
        val adSoyad = intent.getStringExtra(EXTRA_AD_SOYAD) ?: ""
        
        android.util.Log.d("AlarmReceiver", "Showing notification for ID: $notificationId")
        
        // Ana uygulamayı açacak intent
        val mainIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        // Bildirim içeriği oluştur
        val notificationContent = buildString {
            if (firma.isNotEmpty()) append("Firma: $firma\n")
            if (adSoyad.isNotEmpty()) append("Kişi: $adSoyad\n")
            if (description.isNotEmpty()) append("Açıklama: $description")
        }
        
        // Bildirim oluştur
        val notification = NotificationCompat.Builder(context, "ajanda_notifications")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(notificationContent)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationContent))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .build()
        
        // Bildirimi göster
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
        
        android.util.Log.d("AlarmReceiver", "Notification displayed successfully")
    }
}
