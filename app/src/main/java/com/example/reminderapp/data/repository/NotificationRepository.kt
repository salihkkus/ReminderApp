package com.example.reminderapp.data.repository

import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: BilsoftApiService
) {
    
    // API'den bildirimleri getir (şimdilik boş liste döndürüyoruz)
    fun getAllNotifications(): Flow<List<ApiNotificationRequest>> = flowOf(emptyList())
    
    // API'ye yeni bildirim ekle
    suspend fun addNotification(
        token: String,
        firma: String,
        adSoyad: String,
        telefon: String,
        gsm: String,
        aciklama: String,
        tarihSaat: LocalDateTime,
        kullanici: String
    ): Result<ApiNotificationResponse> {
        return try {
            // Tarih formatını ISO 8601'e çevir
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val formattedDate = tarihSaat.format(dateFormatter)
            
            val notificationRequest = ApiNotificationRequest(
                aciklama = aciklama,
                adSoyad = adSoyad,
                cep = gsm,
                firma = firma,
                id = 0, // API otomatik ID atayacak
                okundu = false,
                tarih = formattedDate,
                tel = telefon,
                userId = null // Şimdilik null, gerekirse token'dan çıkarılabilir
            )
            
            val response = apiService.addNotification(token, notificationRequest)
            
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Bildirim eklenemedi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Silme işlemi (şimdilik boş)
    suspend fun deleteNotification(notification: ApiNotificationRequest): Result<Boolean> {
        return Result.success(true)
    }
    
    // Güncelleme işlemi (şimdilik boş)
    suspend fun updateNotification(notification: ApiNotificationRequest): Result<Boolean> {
        return Result.success(true)
    }
}
