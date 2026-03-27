package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class UserService(
    @SerializedName("id") val id: String,           // PK привязки (1506, 1512)
    @SerializedName("__usluga") val serviceName: String,
    @SerializedName("enable_date") val enableDate: String,
    @SerializedName("activated") val activated: String = "1"
)