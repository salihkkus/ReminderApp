package com.example.reminderapp.data.api

import com.example.reminderapp.data.model.ApiResponse
import com.example.reminderapp.data.model.LoginRequest
import com.example.reminderapp.data.model.LoginResponse
import com.example.reminderapp.data.model.Reminder
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationResponse
import com.example.reminderapp.data.model.ApiNotificationListRequest
import com.example.reminderapp.data.model.ApiNotificationListResponse
import retrofit2.http.*

interface BilsoftApiService {
    
    @POST("Auth/GirisYap")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    
    @GET("Ajanda/GetReminders")
    suspend fun getReminders(
        @Header("Authorization") token: String,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): ApiResponse<List<Reminder>>
    
    @POST("Ajanda/CreateReminder")
    suspend fun createReminder(
        @Header("Authorization") token: String,
        @Body reminder: Reminder
    ): ApiResponse<Reminder>
    
    @PUT("Ajanda/UpdateReminder/{id}")
    suspend fun updateReminder(
        @Header("Authorization") token: String,
        @Path("id") id: Long,
        @Body reminder: Reminder
    ): ApiResponse<Reminder>
    
    @DELETE("Ajanda/DeleteReminder/{id}")
    suspend fun deleteReminder(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Boolean>
    
    @PUT("Ajanda/CompleteReminder/{id}")
    suspend fun completeReminder(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): ApiResponse<Boolean>
    
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
