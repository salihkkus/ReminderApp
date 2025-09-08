package com.example.reminderapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val firma: String,                    // Firma adı
    val adSoyad: String,                  // Ad Soyad
    val telefon: String,                  // Telefon numarası
    val gsm: String,                      // GSM numarası
    val aciklama: String,                 // Açıklama
    val tarihSaat: LocalDateTime,         // Tarih ve saat
    val kullanici: String,                // Kullanıcı adı
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
