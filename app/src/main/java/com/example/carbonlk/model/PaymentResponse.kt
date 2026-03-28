package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class PaymentResponse(
    @SerializedName("text") val text: String,
    @SerializedName("type") val type: String
)