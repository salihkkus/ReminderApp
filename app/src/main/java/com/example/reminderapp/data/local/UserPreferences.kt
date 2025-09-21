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
        private const val KEY_AJANDA_NOT_ID = "ajanda_not_id_"
        private const val KEY_PRIORITY_IDS = "priority_ids"
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

    // Tüm verileri temizle ama öncelik listelerini koru
    fun clearAllPreservePriority() {
        val priority = prefs.getStringSet(KEY_PRIORITY_IDS, emptySet()) ?: emptySet()
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
        // clear() tüm verileri sildiği için öncelik setini geri yaz
        prefs.edit().putStringSet(KEY_PRIORITY_IDS, priority).apply()
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
    
    // Ajanda not ID'sini kaydet (ajanda ID -> not ID mapping)
    fun saveAjandaNotId(ajandaId: Int, notId: Int) {
        prefs.edit().putInt("$KEY_AJANDA_NOT_ID$ajandaId", notId).apply()
    }
    
    // Ajanda not ID'sini al
    fun getAjandaNotId(ajandaId: Int): Int? {
        val notId = prefs.getInt("$KEY_AJANDA_NOT_ID$ajandaId", -1)
        return if (notId == -1) null else notId
    }
    
    // Ajanda not ID'sini sil
    fun clearAjandaNotId(ajandaId: Int) {
        prefs.edit().remove("$KEY_AJANDA_NOT_ID$ajandaId").apply()
    }

    // === Önceliklendirme (yıldız) ===
    fun getPrioritizedIds(): Set<Int> {
        val stored = prefs.getStringSet(KEY_PRIORITY_IDS, emptySet()) ?: emptySet()
        return stored.mapNotNull { it.toIntOrNull() }.toSet()
    }

    fun isPrioritized(id: Int): Boolean {
        return getPrioritizedIds().contains(id)
    }

    fun togglePriority(id: Int): Set<Int> {
        val current = getPrioritizedIds().toMutableSet()
        if (current.contains(id)) current.remove(id) else current.add(id)
        val asStrings = current.map { it.toString() }.toSet()
        prefs.edit().putStringSet(KEY_PRIORITY_IDS, asStrings).apply()
        return current
    }
}

data class SavedCredentials(
    val vergiNumarasi: String,
    val kullaniciAdi: String,
    val kullaniciSifre: String,
    val token: String? = null
)
