package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class CreateTicketResponse(
    @SerializedName("message") val message: String
)