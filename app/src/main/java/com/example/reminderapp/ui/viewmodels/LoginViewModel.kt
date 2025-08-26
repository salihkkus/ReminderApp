package com.example.reminderapp.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.model.LoginRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val apiService: BilsoftApiService
) : ViewModel() {
    
    private val _loginState = MutableStateFlow(LoginState())
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()
    
    fun login(
        vergiNumarasi: String,
        kullaniciAdi: String,
        kullaniciSifre: String,
        veritabaniAd: String,
        donemYil: String,
        subeAd: String,
        apiKullaniciAdi: String,
        apiKullaniciSifre: String
    ) {
        // Input validation
        if (vergiNumarasi.isBlank()) {
            _loginState.value = LoginState(error = "Vergi numarası boş olamaz")
            return
        }
        
        if (kullaniciAdi.isBlank()) {
            _loginState.value = LoginState(error = "Kullanıcı adı boş olamaz")
            return
        }
        
        if (kullaniciSifre.isBlank()) {
            _loginState.value = LoginState(error = "Şifre boş olamaz")
            return
        }
        
        // Email format validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(kullaniciAdi).matches()) {
            _loginState.value = LoginState(error = "Geçerli bir e-posta adresi girin")
            return
        }
        
        viewModelScope.launch {
            _loginState.value = LoginState(isLoading = true, error = null)
            
            try {
                val loginRequest = LoginRequest(
                    vergiNumarasi = vergiNumarasi.trim(),
                    kullaniciAdi = kullaniciAdi.trim(),
                    kullaniciSifre = kullaniciSifre,
                    veritabaniAd = veritabaniAd,
                    donemYil = donemYil,
                    subeAd = subeAd,
                    apiKullaniciAdi = apiKullaniciAdi,
                    apiKullaniciSifre = apiKullaniciSifre
                )
                
                val response = apiService.login(loginRequest)
                
                if (response.success && response.token != null) {
                    // Store token securely (you might want to use DataStore or EncryptedSharedPreferences)
                    _loginState.value = LoginState(isSuccess = true)
                } else {
                    _loginState.value = LoginState(
                        error = response.message ?: "Giriş başarısız. Bilgilerinizi kontrol edin."
                    )
                }
            } catch (e: Exception) {
                _loginState.value = LoginState(
                    error = "Bağlantı hatası: ${e.message ?: "Bilinmeyen hata"}"
                )
            }
        }
    }
    
    fun resetState() {
        _loginState.value = LoginState()
    }
    
    fun clearError() {
        _loginState.value = _loginState.value.copy(error = null)
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
