package com.example.reminderapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationData
import com.example.reminderapp.data.repository.NotificationRepository
import com.example.reminderapp.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val tokenManager: TokenManager
) : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<ApiNotificationData>>(emptyList())
    val notifications: StateFlow<List<ApiNotificationData>> = _notifications.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        loadNotifications()
    }
    
    fun loadNotifications() {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            _error.value = "Kullanıcı giriş yapmamış"
            return
        }
        
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val result = repository.getAllNotifications(token)
                
                result.fold(
                    onSuccess = { notificationList ->
                        _notifications.value = notificationList
                        Log.d("NotificationViewModel", "Loaded ${notificationList.size} notifications")
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error loading notifications", error)
                        _error.value = error.message ?: "Bildirimler yüklenemedi"
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error loading notifications", e)
                _error.value = e.message ?: "Bildirimler yüklenemedi"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    fun addNotification(
        firma: String,
        adSoyad: String,
        telefon: String,
        gsm: String,
        aciklama: String,
        tarihSaat: LocalDateTime,
        kullanici: String
    ) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            return
        }
        
        viewModelScope.launch {
            try {
                val result = repository.addNotification(
                    token = "Bearer $token",
                    firma = firma.trim(),
                    adSoyad = adSoyad.trim(),
                    telefon = telefon.trim(),
                    gsm = gsm.trim(),
                    aciklama = aciklama.trim(),
                    tarihSaat = tarihSaat,
                    kullanici = kullanici.trim()
                )
                
                result.fold(
                    onSuccess = { response ->
                        Log.d("NotificationViewModel", "Notification added successfully: ${response.message}")
                        // Bildirim eklendikten sonra listeyi yenile
                        loadNotifications()
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error adding notification", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error adding notification", e)
            }
        }
    }
    
    fun deleteNotification(notification: ApiNotificationData) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    Log.e("NotificationViewModel", "Token not found! User must be logged in.")
                    return@launch
                }
                val result = repository.deleteNotification(token, notification)
                result.fold(
                    onSuccess = { 
                        Log.d("NotificationViewModel", "Notification deleted: ${notification.id}")
                        // Bildirim silindikten sonra listeyi yenile
                        loadNotifications()
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error deleting notification", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error deleting notification", e)
            }
        }
    }
    
    fun updateNotification(notification: ApiNotificationData) {
        viewModelScope.launch {
            try {
                val result = repository.updateNotification(notification)
                result.fold(
                    onSuccess = { 
                        Log.d("NotificationViewModel", "Notification updated: ${notification.id}")
                        // Bildirim güncellendikten sonra listeyi yenile
                        loadNotifications()
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error updating notification", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error updating notification", e)
            }
        }
    }
}