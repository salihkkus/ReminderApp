package com.example.reminderapp.ui.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.reminderapp.data.api.BilsoftApiService
import com.example.reminderapp.data.model.LoginRequest
import com.example.reminderapp.data.model.UserInfo
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
        kullaniciSifre: String
    ) {
        // Input validation
        if (vergiNumarasi.isBlank() || kullaniciAdi.isBlank() || kullaniciSifre.isBlank()) {
            _loginState.value = LoginState(error = "Lütfen tüm alanları doldurun")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "Login attempt started")
                _loginState.value = LoginState(isLoading = true, error = null)
                
                // API için gerekli olan ek alanları otomatik olarak doldur
                val loginRequest = LoginRequest(
                    vergiNumarasi = vergiNumarasi.trim(),
                    kullaniciAdi = kullaniciAdi.trim(),
                    kullaniciSifre = kullaniciSifre.trim(),
                    veritabaniAd = vergiNumarasi.trim(), // Vergi numarası ile aynı
                    donemYil = "2025", // Sabit değer
                    subeAd = "Merkez", // Sabit değer
                    apiKullaniciAdi = "BLS-d475b5037621", // Sabit değer
                    apiKullaniciSifre = "e9d251eb-8d86-4e83-95d5-7163f141f8d3" // Sabit değer
                )
                
                Log.d("LoginViewModel", "Sending login request to API")
                val response = apiService.login(loginRequest)
                
                Log.d("LoginViewModel", "API Response: success=${response.success}, message=${response.message}")
                
                if (response.success == true && response.data?.token != null) {
                    Log.d("LoginViewModel", "Login successful, token received")
                    _loginState.value = LoginState(
                        isSuccess = true,
                        token = response.data.token,
                        userInfo = response.data.userInfo
                    )
                    Log.d("LoginViewModel", "Login state updated to success")
                } else {
                    // API'den gelen hata mesajını göster
                    val errorMessage = response.message ?: "Giriş başarısız"
                    Log.d("LoginViewModel", "Login failed: $errorMessage")
                    _loginState.value = LoginState(
                        error = errorMessage,
                        apiCode = response.code
                    )
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Login error", e)
                val errorMessage = when (e) {
                    is java.net.UnknownHostException -> "İnternet bağlantısı yok"
                    is java.net.SocketTimeoutException -> "Bağlantı zaman aşımı"
                    is retrofit2.HttpException -> "Sunucu hatası: ${e.code()}"
                    else -> "Bağlantı hatası: ${e.message ?: "Bilinmeyen hata"}"
                }
                _loginState.value = LoginState(error = errorMessage)
            }
        }
    }
    
    // Test için mock login (API çalışmazsa kullan)
    fun testLogin() {
        Log.d("LoginViewModel", "Test login triggered")
        viewModelScope.launch {
            try {
                _loginState.value = LoginState(isLoading = true, error = null)
                
                // Simulate API delay
                kotlinx.coroutines.delay(1000)
                
                // Mock successful response
                _loginState.value = LoginState(
                    isSuccess = true,
                    token = "test_token_12345",
                    userInfo = UserInfo(
                        userId = "test_user_123",
                        userName = "Test Kullanıcı",
                        email = "test@example.com"
                    )
                )
                
                Log.d("LoginViewModel", "Test login successful")
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Test login error", e)
                _loginState.value = LoginState(error = "Test giriş hatası: ${e.message}")
            }
        }
    }
    
    fun resetState() {
        Log.d("LoginViewModel", "Resetting login state")
        _loginState.value = LoginState()
    }
}

data class LoginState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null,
    val token: String? = null,
    val userInfo: com.example.reminderapp.data.model.UserInfo? = null,
    val apiCode: String? = null
)
