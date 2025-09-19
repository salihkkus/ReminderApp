package com.example.reminderapp.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "user_preferences", 
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_REMEMBER_ME = "remember_me"
        private const val KEY_VERGI_NUMARASI = "vergi_numarasi"
        private const val KEY_KULLANICI_ADI = "kullanici_adi"
        private const val KEY_KULLANICI_SIFRE = "kullanici_sifre"
        private const val KEY_TOKEN = "token"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    // Beni Hatırla durumu
    fun setRememberMe(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_REMEMBER_ME, enabled).apply()
    }

    fun getRememberMe(): Boolean {
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    // Kullanıcı bilgilerini kaydet
    fun saveUserCredentials(
        vergiNumarasi: String,
        kullaniciAdi: String,
        kullaniciSifre: String,
        token: String? = null
    ) {
        val editor = prefs.edit()
        editor.putString(KEY_VERGI_NUMARASI, vergiNumarasi)
        editor.putString(KEY_KULLANICI_ADI, kullaniciAdi)
        editor.putString(KEY_KULLANICI_SIFRE, kullaniciSifre)
        token?.let { editor.putString(KEY_TOKEN, it) }
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    // Kaydedilmiş kullanıcı bilgilerini al
    fun getSavedCredentials(): SavedCredentials? {
        val vergiNumarasi = prefs.getString(KEY_VERGI_NUMARASI, null)
        val kullaniciAdi = prefs.getString(KEY_KULLANICI_ADI, null)
        val kullaniciSifre = prefs.getString(KEY_KULLANICI_SIFRE, null)
        val token = prefs.getString(KEY_TOKEN, null)

        return if (vergiNumarasi != null && kullaniciAdi != null && kullaniciSifre != null) {
            SavedCredentials(
                vergiNumarasi = vergiNumarasi,
                kullaniciAdi = kullaniciAdi,
                kullaniciSifre = kullaniciSifre,
                token = token
            )
        } else null
    }

    // Giriş durumu
    fun setLoggedIn(isLoggedIn: Boolean) {
        prefs.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    // Token kaydetme/alma
    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    // Tüm verileri temizle (çıkış yaparken)
    fun clearAll() {
        prefs.edit().clear().apply()
    }

    // Sadece giriş bilgilerini temizle (token'ı koru)
    fun clearCredentials() {
        prefs.edit()
            .remove(KEY_VERGI_NUMARASI)
            .remove(KEY_KULLANICI_ADI)
            .remove(KEY_KULLANICI_SIFRE)
            .remove(KEY_IS_LOGGED_IN)
            .apply()
    }
}

data class SavedCredentials(
    val vergiNumarasi: String,
    val kullaniciAdi: String,
    val kullaniciSifre: String,
    val token: String? = null
)
