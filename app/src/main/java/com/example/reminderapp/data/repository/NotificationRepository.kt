package com.example.reminderapp.data.repository

import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationResponse
import com.example.reminderapp.data.model.ApiNotificationListRequest
import com.example.reminderapp.data.model.ApiNotificationListResponse
import com.example.reminderapp.data.model.ApiNotificationData
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
    
    // API'den bildirimleri getir - farklı sayfa numaralarını dene
    suspend fun getAllNotifications(token: String): Result<List<ApiNotificationData>> {
        return try {
            // Önce sayfa 0'ı dene (bazen API'ler 0'dan başlar)
            val request = ApiNotificationListRequest(
                orderBy = "tarih",
                desc = true,
                pagingOptions = com.example.reminderapp.data.model.PagingOptions(
                    pageNumber = 0, // 0'dan başla
                    pageSize = 1000
                ),
                baslangicTarih = null,
                bitisTarih = null,
                aranacakKelime = null,
                aranacakKelimeIncludes = null,
                aranacakKelimeSutuns = null
            )
            
            val response = apiService.getAllNotifications("Bearer $token", request)
            
            // Debug logging
            android.util.Log.d("NotificationRepository", "API Response: success=${response.success}")
            android.util.Log.d("NotificationRepository", "API Response: totalCount=${response.totalCount}")
            android.util.Log.d("NotificationRepository", "API Response: data size=${response.data?.size ?: 0}")
            android.util.Log.d("NotificationRepository", "API Response: message=${response.message}")
            android.util.Log.d("NotificationRepository", "API Response: code=${response.code}")
            
            if (response.success && response.data != null && response.data.isNotEmpty()) {
                Result.success(response.data)
            } else if (response.success && response.totalCount > 0) {
                // Eğer totalCount > 0 ama data boşsa, sayfa 1'i dene
                android.util.Log.d("NotificationRepository", "Trying page 1...")
                val requestPage1 = request.copy(
                    pagingOptions = com.example.reminderapp.data.model.PagingOptions(
                        pageNumber = 1,
                        pageSize = 1000
                    )
                )
                val responsePage1 = apiService.getAllNotifications("Bearer $token", requestPage1)
                
                if (responsePage1.success && responsePage1.data != null) {
                    Result.success(responsePage1.data)
                } else {
                    Result.failure(Exception("Sayfa 1'de de veri bulunamadı"))
                }
            } else {
                Result.failure(Exception(response.message ?: "Bildirimler alınamadı"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
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
    suspend fun deleteNotification(notification: ApiNotificationData): Result<Boolean> {
        return Result.success(true)
    }
    
    // Güncelleme işlemi (şimdilik boş)
    suspend fun updateNotification(notification: ApiNotificationData): Result<Boolean> {
        return Result.success(true)
    }
}
