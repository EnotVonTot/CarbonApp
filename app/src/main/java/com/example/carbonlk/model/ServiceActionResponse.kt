package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class ServiceActionResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: String? = null
)