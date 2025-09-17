package com.example.reminderapp.data.api

import com.example.reminderapp.data.model.LoginRequest
import com.example.reminderapp.data.model.LoginResponse
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationResponse
import com.example.reminderapp.data.model.ApiNotificationListRequest
import com.example.reminderapp.data.model.ApiNotificationListResponse
import retrofit2.http.*

interface BilsoftApiService {
    
    @POST("Auth/GirisYap")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    
    // Resmi olmayan Reminder endpointleri kaldırıldı
    
    // removed unused completeReminder endpoint
    
    // === BİLDİRİM İŞLEMLERİ ===
    
    @POST("Ajanda/add")
    suspend fun addNotification(
        @Header("Authorization") token: String,
        @Body notification: ApiNotificationRequest
    ): ApiNotificationResponse
    
    @POST("Ajanda/getall")
    suspend fun getAllNotifications(
        @Header("Authorization") token: String,
        @Body request: ApiNotificationListRequest
    ): ApiNotificationListResponse

    @POST("Ajanda/delete")
    suspend fun deleteNotification(
        @Header("Authorization") token: String,
        @Body request: ApiNotificationRequest
    ): ApiNotificationResponse

    @PUT("Ajanda/update")
    suspend fun updateNotification(
        @Header("Authorization") token: String,
        @Body request: ApiNotificationRequest
    ): ApiNotificationResponse
}
