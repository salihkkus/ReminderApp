package com.example.reminderapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.model.ApiNotificationRequest
import com.example.reminderapp.data.model.ApiNotificationData
import com.example.reminderapp.data.model.AjandaNotData
import com.example.reminderapp.data.repository.NotificationRepository
import com.example.reminderapp.data.local.TokenManager
import com.example.reminderapp.data.local.UserPreferences
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
    private val tokenManager: TokenManager,
    private val userPreferences: UserPreferences
) : ViewModel() {
    
    private val _notifications = MutableStateFlow<List<ApiNotificationData>>(emptyList())
    val notifications: StateFlow<List<ApiNotificationData>> = _notifications.asStateFlow()
    
    // Ajanda notları için state - her bildirim için ayrı not
    private val _ajandaNotlar = MutableStateFlow<Map<Int, AjandaNotData>>(emptyMap())
    val ajandaNotlar: StateFlow<Map<Int, AjandaNotData>> = _ajandaNotlar.asStateFlow()
    
    // Tek bir ajanda notu için (geriye dönük uyumluluk)
    private val _ajandaNot = MutableStateFlow<AjandaNotData?>(null)
    val ajandaNot: StateFlow<AjandaNotData?> = _ajandaNot.asStateFlow()
    
    // Not ID'sini sakla (ajandaId ile notId farklı olabilir)
    private val _currentNotId = MutableStateFlow<Int?>(null)
    val currentNotId: StateFlow<Int?> = _currentNotId.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    init {
        // Load initial data
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
                        
                        // Her bildirim için ajanda notunu yükle
                        loadAjandaNotlarForNotifications(notificationList)
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
    
    // Her bildirim için ajanda notlarını yükle
    private fun loadAjandaNotlarForNotifications(notifications: List<ApiNotificationData>) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) return
        
        viewModelScope.launch {
            val ajandaNotlarMap = mutableMapOf<Int, AjandaNotData>()
            
            notifications.forEach { notification ->
                try {
                    // Kaydedilmiş not ID'sini kontrol et
                    val savedNotId = userPreferences.getAjandaNotId(notification.id)
                    val notId = savedNotId ?: notification.id
                    
                    val result = repository.getAjandaNotById(
                        token = "Bearer $token",
                        id = notId
                    )
                    
                    result.fold(
                        onSuccess = { response ->
                            if (response.data != null) {
                                ajandaNotlarMap[notification.id] = response.data
                                Log.d("NotificationViewModel", "Loaded ajanda not for notification ${notification.id}")
                            }
                        },
                        onFailure = { error ->
                            Log.d("NotificationViewModel", "No ajanda not found for notification ${notification.id}")
                        }
                    )
                } catch (e: Exception) {
                    Log.d("NotificationViewModel", "Error loading ajanda not for notification ${notification.id}")
                }
            }
            
            _ajandaNotlar.value = ajandaNotlarMap
            Log.d("NotificationViewModel", "Loaded ${ajandaNotlarMap.size} ajanda notlar")
        }
    }
    
    fun addNotification(
        firma: String,
        adSoyad: String,
        telefon: String,
        gsm: String,
        aciklama: String,
        tarihSaat: LocalDateTime,
        kullanici: String,
        tamamlandi: Boolean = false
    ) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            return
        }
        
        viewModelScope.launch {
            try {
                val result = repository.addNotificationWithAlarm(
                    token = "Bearer $token",
                    firma = firma.trim(),
                    adSoyad = adSoyad.trim(),
                    telefon = telefon.trim(),
                    gsm = gsm.trim(),
                    aciklama = aciklama.trim(),
                    tarihSaat = tarihSaat,
                    kullanici = kullanici.trim(),
                    tamamlandi = tamamlandi
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
                when (e) {
                    is kotlinx.coroutines.CancellationException -> {
                        Log.w("NotificationViewModel", "Notification add operation was cancelled")
                        // CancellationException'ı yeniden fırlatma, bu normal bir durum
                    }
                    else -> {
                        Log.e("NotificationViewModel", "Error adding notification", e)
                    }
                }
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
                when (e) {
                    is kotlinx.coroutines.CancellationException -> {
                        Log.w("NotificationViewModel", "Notification delete operation was cancelled")
                    }
                    else -> {
                        Log.e("NotificationViewModel", "Error deleting notification", e)
                    }
                }
            }
        }
    }
    
    fun updateNotification(notification: ApiNotificationData) {
        viewModelScope.launch {
            try {
                val token = tokenManager.getToken()
                if (token.isNullOrBlank()) {
                    Log.e("NotificationViewModel", "Token not found! User must be logged in.")
                    return@launch
                }
                val result = repository.updateNotification(token, notification)
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
                when (e) {
                    is kotlinx.coroutines.CancellationException -> {
                        Log.w("NotificationViewModel", "Notification update operation was cancelled")
                    }
                    else -> {
                        Log.e("NotificationViewModel", "Error updating notification", e)
                    }
                }
            }
        }
    }
    
    // === AJANDA NOT İŞLEMLERİ ===
    
    fun addAjandaNot(ajandaId: String, notlar: String) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            return
        }
        
        viewModelScope.launch {
            try {
                val result = repository.addAjandaNot(
                    token = "Bearer $token",
                    ajandaId = ajandaId,
                    notlar = notlar
                )
                
                result.fold(
                    onSuccess = { response ->
                        Log.d("NotificationViewModel", "Ajanda not added successfully: ${response.message}")
                        // Eklenen notu state'e kaydet
                        if (response.data != null) {
                            _ajandaNot.value = response.data
                            _currentNotId.value = response.data.id
                            
                            // Not ID'sini kalıcı olarak sakla (ajanda ID -> not ID mapping)
                            val ajandaIdInt = ajandaId.toIntOrNull()
                            if (ajandaIdInt != null) {
                                userPreferences.saveAjandaNotId(ajandaIdInt, response.data.id)
                                Log.d("NotificationViewModel", "Not ID ${response.data.id} saved for ajanda ID $ajandaIdInt")
                            }
                            
                            Log.d("NotificationViewModel", "Not saved to state with ID: ${response.data.id}")
                        }
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error adding ajanda not", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error adding ajanda not", e)
            }
        }
    }
    
    // === ÖNCELİK (YILDIZ) ===
    private val _priorityIds = MutableStateFlow(userPreferences.getPrioritizedIds())
    val priorityIds: StateFlow<Set<Int>> = _priorityIds.asStateFlow()

    fun togglePriority(notificationId: Int) {
        val updated = userPreferences.togglePriority(notificationId)
        _priorityIds.value = updated
    }

    // Ajanda notu getir (ID ile)
    fun getAjandaNotById(ajandaId: Int) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            return
        }
        
        viewModelScope.launch {
            try {
                // Önce SharedPreferences'ten kaydedilmiş not ID'sini kontrol et
                val savedNotId = userPreferences.getAjandaNotId(ajandaId)
                
                // Eğer currentNotId varsa onu kullan, yoksa kaydedilmiş not ID'sini kullan, son çare olarak ajandaId'yi dene
                val notId = _currentNotId.value ?: savedNotId ?: ajandaId
                
                Log.d("NotificationViewModel", "Getting not with ID: $notId (currentNotId: ${_currentNotId.value}, savedNotId: $savedNotId, ajandaId: $ajandaId)")
                
                val result = repository.getAjandaNotById(
                    token = "Bearer $token",
                    id = notId
                )
                
                result.fold(
                    onSuccess = { response ->
                        Log.d("NotificationViewModel", "Ajanda not retrieved successfully")
                        if (response.data != null) {
                            _ajandaNot.value = response.data
                            _currentNotId.value = response.data.id
                        }
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error getting ajanda not", error)
                        // Hata durumunda state'i temizle
                        _ajandaNot.value = null
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error getting ajanda not", e)
                _ajandaNot.value = null
            }
        }
    }
    
    // Ajanda notu güncelle
    fun updateAjandaNot(notId: Int, ajandaId: String, notlar: String) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            return
        }
        
        viewModelScope.launch {
            try {
                val result = repository.updateAjandaNot(
                    token = "Bearer $token",
                    notId = notId,
                    ajandaId = ajandaId,
                    notlar = notlar
                )
                
                result.fold(
                    onSuccess = { response ->
                        Log.d("NotificationViewModel", "Ajanda not updated successfully: ${response.message}")
                        // Güncellenmiş notu state'e kaydet
                        if (response.data != null) {
                            _ajandaNot.value = response.data
                            Log.d("NotificationViewModel", "Not updated in state with ID: ${response.data.id}")
                        }
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error updating ajanda not", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error updating ajanda not", e)
            }
        }
    }
    
    // Ajanda notu sil
    fun deleteAjandaNot(notId: Int, ajandaId: String, notlar: String) {
        val token = tokenManager.getToken()
        if (token.isNullOrBlank()) {
            Log.e("NotificationViewModel", "Token not found! User must be logged in.")
            return
        }
        
        viewModelScope.launch {
            try {
                val result = repository.deleteAjandaNot(
                    token = "Bearer $token",
                    notId = notId,
                    ajandaId = ajandaId,
                    notlar = notlar
                )
                
                result.fold(
                    onSuccess = { response ->
                        Log.d("NotificationViewModel", "Ajanda not deleted successfully: ${response.message}")
                        // State'i temizle
                        _ajandaNot.value = null
                        _currentNotId.value = null
                        
                        // SharedPreferences'ten de sil
                        val ajandaIdInt = ajandaId.toIntOrNull()
                        if (ajandaIdInt != null) {
                            userPreferences.clearAjandaNotId(ajandaIdInt)
                            Log.d("NotificationViewModel", "Not ID cleared for ajanda ID $ajandaIdInt")
                        }
                    },
                    onFailure = { error ->
                        Log.e("NotificationViewModel", "Error deleting ajanda not", error)
                    }
                )
            } catch (e: Exception) {
                Log.e("NotificationViewModel", "Error deleting ajanda not", e)
            }
        }
    }
}