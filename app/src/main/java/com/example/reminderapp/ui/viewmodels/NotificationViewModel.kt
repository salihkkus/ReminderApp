package com.example.reminderapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.repository.NotificationRepository
import com.example.reminderapp.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    
    val notifications: StateFlow<List<ApiNotificationRequest>> = repository
        .getAllNotifications()
        .catch { error ->
            Log.e("NotificationViewModel", "Error loading notifications", error)
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
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
    
    fun deleteNotification(notification: ApiNotificationRequest) {
        viewModelScope.launch {
            try {
                val result = repository.deleteNotification(notification)
                result.fold(
                    onSuccess = { 
                        Log.d("NotificationViewModel", "Notification deleted: ${notification.id}")
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
    
    fun updateNotification(notification: ApiNotificationRequest) {
        viewModelScope.launch {
            try {
                val result = repository.updateNotification(notification)
                result.fold(
                    onSuccess = { 
                        Log.d("NotificationViewModel", "Notification updated: ${notification.id}")
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