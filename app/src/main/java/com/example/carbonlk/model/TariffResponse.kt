package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class TariffResponse(
    @SerializedName("tarif") val tariff: String
)