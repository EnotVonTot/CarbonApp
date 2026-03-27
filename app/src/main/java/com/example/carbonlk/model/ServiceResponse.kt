package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

// Общий ответ от API (массив из трёх массивов)
typealias ServicesListResponse = List<List<Any>>

// Если нужна отдельная модель для ответа API
data class ServiceResponse(
    @SerializedName("services") val services: List<AvailableService>? = null,
    @SerializedName("user_services") val userServices: List<UserService>? = null
)