package com.example.reminderapp.data.repository

import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationResponse
import com.example.reminderapp.data.model.ApiNotificationListRequest
import com.example.reminderapp.data.model.ApiNotificationListResponse
import com.example.reminderapp.data.model.ApiNotificationData
import com.example.reminderapp.data.model.AjandaNotRequest
import com.example.reminderapp.data.model.AjandaNotResponse
import com.example.reminderapp.receivers.AlarmScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val apiService: BilsoftApiService,
    private val alarmScheduler: AlarmScheduler
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
        kullanici: String,
        tamamlandi: Boolean = false
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
                okundu = tamamlandi,
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
    
    // Bildirim ekleme işlemi - API başarılı olursa alarm kur
    suspend fun addNotificationWithAlarm(
        token: String,
        firma: String,
        adSoyad: String,
        telefon: String,
        gsm: String,
        aciklama: String,
        tarihSaat: LocalDateTime,
        kullanici: String,
        tamamlandi: Boolean = false
    ): Result<ApiNotificationResponse> {
        val result = addNotification(token, firma, adSoyad, telefon, gsm, aciklama, tarihSaat, kullanici, tamamlandi)
        
        result.fold(
            onSuccess = { response ->
                // API başarılı olursa alarm kur
                response.data?.let { notificationData ->
                    alarmScheduler.scheduleNotification(notificationData)
                }
            },
            onFailure = { /* Hata durumunda alarm kurma */ }
        )
        
        return result
    }
    
    // Silme işlemi - API başarılı olursa alarm iptal et
    suspend fun deleteNotification(token: String, notification: ApiNotificationData): Result<Boolean> {
        return try {
            val request = ApiNotificationRequest(
                aciklama = notification.aciklama,
                adSoyad = notification.adSoyad,
                cep = notification.cep,
                firma = notification.firma,
                id = notification.id,
                okundu = notification.okundu,
                tarih = notification.tarih,
                tel = notification.tel,
                userId = notification.userId
            )
            val response = apiService.deleteNotification("Bearer $token", request)
            if (response.success) {
                // API başarılı olursa alarm iptal et
                alarmScheduler.cancelNotification(notification.id)
                Result.success(true)
            } else {
                Result.failure(Exception(response.message ?: "Silme başarısız"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Güncelleme işlemi - API başarılı olursa eski alarmı iptal et, yenisini kur
    suspend fun updateNotification(token: String, notification: ApiNotificationData): Result<Boolean> {
        return try {
            val request = ApiNotificationRequest(
                aciklama = notification.aciklama,
                adSoyad = notification.adSoyad,
                cep = notification.cep,
                firma = notification.firma,
                id = notification.id,
                okundu = notification.okundu,
                tarih = notification.tarih,
                tel = notification.tel,
                userId = notification.userId
            )
            val response = apiService.updateNotification("Bearer $token", request)
            if (response.success) {
                // API başarılı olursa alarm güncelle
                alarmScheduler.cancelNotification(notification.id)
                alarmScheduler.scheduleNotification(notification)
                Result.success(true)
            } else {
                Result.failure(Exception(response.message ?: "Güncelleme başarısız"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // === AJANDA NOT İŞLEMLERİ ===
    
    // Ajanda notu ekle
    suspend fun addAjandaNot(
        token: String,
        ajandaId: String,
        notlar: String
    ): Result<AjandaNotResponse> {
        return try {
            val request = AjandaNotRequest(
                ajandaId = ajandaId,
                id = 0, // API otomatik ID atayacak
                notlar = notlar
            )
            val response = apiService.addAjandaNot("Bearer $token", request)
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Not eklenemedi"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Ajanda notu getir (ID ile)
    suspend fun getAjandaNotById(
        token: String,
        id: Int
    ): Result<AjandaNotResponse> {
        return try {
            val response = apiService.getAjandaNotById("Bearer $token", id)
            if (response.success) {
                Result.success(response)
            } else {
                Result.failure(Exception(response.message ?: "Not bulunamadı"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
