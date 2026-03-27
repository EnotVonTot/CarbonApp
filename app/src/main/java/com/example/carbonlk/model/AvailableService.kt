package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class AvailableService(
    @SerializedName("pk") val id: String,           // PK самой услуги (12, 86)
    @SerializedName("name") val name: String,
    @SerializedName("__self") val fullName: String,
    @SerializedName("comments") val description: String? = null,
    @SerializedName("summa") val price: String,
    @SerializedName("is_show_web") val isShowWeb: String = "1",
    @SerializedName("web_allow_disable") val webAllowDisable: String = "0",
    var isConnected: Boolean = false
)