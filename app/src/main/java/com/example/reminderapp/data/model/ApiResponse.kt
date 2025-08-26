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

data class LoginResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("token")
    val token: String?,
    @SerializedName("expiresAt")
    val expiresAt: String?
)

data class ApiResponse<T>(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: T?
)
