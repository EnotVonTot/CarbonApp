package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class BalanceResponse(
    @SerializedName("balance") val balance: String
)