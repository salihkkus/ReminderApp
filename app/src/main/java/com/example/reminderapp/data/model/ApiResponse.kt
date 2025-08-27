package com.example.reminderapp.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("vergiNumarasi")
    val vergiNumarasi: String,
    @SerializedName("kullaniciAdi")
    val kullaniciAdi: String,
    @SerializedName("kullaniciSifre")
    val kullaniciSifre: String,
    @SerializedName("veritabaniAd")
    val veritabaniAd: String,
    @SerializedName("donemYil")
    val donemYil: String,
    @SerializedName("subeAd")
    val subeAd: String,
    @SerializedName("apiKullaniciAdi")
    val apiKullaniciAdi: String,
    @SerializedName("apiKullaniciSifre")
    val apiKullaniciSifre: String
)

// Gerçek API response formatına göre güncellendi
data class LoginResponse(
    @SerializedName("errorDetail")
    val errorDetail: String? = null,
    @SerializedName("data")
    val data: LoginData? = null,
    @SerializedName("totalCount")
    val totalCount: Int = 0,
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("code")
    val code: String? = null
)

data class LoginData(
    @SerializedName("token")
    val token: String? = null,
    @SerializedName("expiresAt")
    val expiresAt: String? = null,
    @SerializedName("userInfo")
    val userInfo: UserInfo? = null
)

data class UserInfo(
    @SerializedName("userId")
    val userId: String? = null,
    @SerializedName("userName")
    val userName: String? = null,
    @SerializedName("email")
    val email: String? = null
)

// Generic API response for other endpoints
data class ApiResponse<T>(
    @SerializedName("errorDetail")
    val errorDetail: String? = null,
    @SerializedName("data")
    val data: T? = null,
    @SerializedName("totalCount")
    val totalCount: Int = 0,
    @SerializedName("success")
    val success: Boolean = false,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("code")
    val code: String? = null
)
