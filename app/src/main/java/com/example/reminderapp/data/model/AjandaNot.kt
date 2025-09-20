package com.example.reminderapp.data.model

import com.google.gson.annotations.SerializedName

// Ajanda Not API Request Modeli
data class AjandaNotRequest(
    @SerializedName("ajandaId")
    val ajandaId: String?,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("notlar")
    val notlar: String?
)

// Ajanda Not API Response Modeli
data class AjandaNotResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: AjandaNotData?,
    @SerializedName("code")
    val code: String?
)

// Ajanda Not Data Modeli
data class AjandaNotData(
    @SerializedName("ajandaId")
    val ajandaId: String?,
    @SerializedName("id")
    val id: Int,
    @SerializedName("notlar")
    val notlar: String?
)
