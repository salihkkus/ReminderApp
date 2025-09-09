package com.example.reminderapp.data.model

import com.google.gson.annotations.SerializedName

// API'ye gönderilecek bildirim modeli
data class ApiNotificationRequest(
    @SerializedName("aciklama")
    val aciklama: String?,
    @SerializedName("adSoyad")
    val adSoyad: String?,
    @SerializedName("cep")
    val cep: String?,
    @SerializedName("firma")
    val firma: String?,
    @SerializedName("id")
    val id: Int = 0,
    @SerializedName("okundu")
    val okundu: Boolean? = false,
    @SerializedName("tarih")
    val tarih: String?, // ISO 8601 format: "2025-05-04T09:42:00Z"
    @SerializedName("tel")
    val tel: String?,
    @SerializedName("userId")
    val userId: Int? = null
)

// API'den gelen bildirim yanıtı
data class ApiNotificationResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: ApiNotificationData?,
    @SerializedName("code")
    val code: String?
)

// API'den bildirim listesi için request modeli
data class ApiNotificationListRequest(
    @SerializedName("aranacakKelime")
    val aranacakKelime: String? = null,
    @SerializedName("aranacakKelimeIncludes")
    val aranacakKelimeIncludes: List<String>? = null,
    @SerializedName("aranacakKelimeInt")
    val aranacakKelimeInt: Int? = null,
    @SerializedName("aranacakKelimeSutuns")
    val aranacakKelimeSutuns: List<String>? = null,
    @SerializedName("baslangicTarih")
    val baslangicTarih: String? = null,
    @SerializedName("bitisTarih")
    val bitisTarih: String? = null,
    @SerializedName("desc")
    val desc: Boolean = true, // Varsayılan olarak azalan sıralama
    @SerializedName("includes")
    val includes: List<String>? = null,
    @SerializedName("nullFiltrelemeYapilacaklar")
    val nullFiltrelemeYapilacaklar: List<String>? = null,
    @SerializedName("orderBy")
    val orderBy: String? = "tarih", // Varsayılan olarak tarihe göre sırala
    @SerializedName("pagingOptions")
    val pagingOptions: PagingOptions? = PagingOptions(),
    @SerializedName("searchType")
    val searchType: List<String>? = null,
    @SerializedName("subeAdi")
    val subeAdi: String? = null,
    @SerializedName("tarihSutunAdi")
    val tarihSutunAdi: String? = "tarih"
)

data class PagingOptions(
    @SerializedName("pageNumber")
    val pageNumber: Int = 1,
    @SerializedName("pageSize")
    val pageSize: Int = 50
)

// API'den gelen bildirim listesi yanıtı
data class ApiNotificationListResponse(
    @SerializedName("success")
    val success: Boolean,
    @SerializedName("message")
    val message: String?,
    @SerializedName("data")
    val data: List<ApiNotificationData>?,
    @SerializedName("totalCount")
    val totalCount: Int,
    @SerializedName("code")
    val code: String?
)
