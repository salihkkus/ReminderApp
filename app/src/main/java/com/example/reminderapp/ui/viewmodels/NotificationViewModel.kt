package com.example.reminderapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.Notification
import com.example.reminderapp.data.repository.NotificationRepository
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
    private val repository: NotificationRepository
) : ViewModel() {
    
    val notifications: StateFlow<List<Notification>> = repository
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
        viewModelScope.launch {
            try {
                val notification = Notification(
                    firma = firma.trim(),
                    adSoyad = adSoyad.trim(),
                    telefon = telefon.trim(),
                    gsm = gsm.trim(),
                    aciklama = aciklama.trim(),
                    tarihSaat = tarihSaat,
                    kullanici = kullanici.trim()
                )
                
                repository.insertNotification(notification)
                Log.d("NotificationViewModel", "Bildirim başarıyla eklendi")
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error adding notification", e)
            }
        }
    }
    
    fun deleteNotification(notification: Notification) {
        viewModelScope.launch {
            try {
                repository.deleteNotification(notification)
                Log.d("NotificationViewModel", "Notification deleted: ${notification.id}")
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error deleting notification", e)
            }
        }
    }
    
    fun updateNotification(notification: Notification) {
        viewModelScope.launch {
            try {
                repository.updateNotification(notification)
                Log.d("NotificationViewModel", "Notification updated: ${notification.id}")
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error updating notification", e)
            }
        }
    }
}
