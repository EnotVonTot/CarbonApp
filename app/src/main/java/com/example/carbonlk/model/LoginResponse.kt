package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("session_id") val sessionId: String,  // ← это поле обязательно!
    @SerializedName("message") val message: String? = null,
    @SerializedName("user") val user: UserData? = null,
    @SerializedName("currency") val currency: CurrencyData? = null
)

data class UserData(
    @SerializedName("login") val login: String,
    @SerializedName("__self") val fullName: String? = null,
    @SerializedName("enabled") val enabled: String? = null,
    @SerializedName("abonent") val abonent: AbonentData? = null,
    @SerializedName("phone") val phone: String? = null,
    @SerializedName("email") val email: String? = null
)

data class AbonentData(
    @SerializedName("name") val name: String,
    @SerializedName("contract_number") val contractNumber: String? = null,
    @SerializedName("activate_date") val activateDate: String? = null,
    @SerializedName("account") val account: AccountData? = null,
    @SerializedName("tarif") val tarif: TarifData? = null
)

data class AccountData(
    @SerializedName("__self") val accountNumber: String? = null
)

data class TarifData(
    @SerializedName("__tarif") val tariffName: String? = null
)

data class CurrencyData(
    @SerializedName("NAME") val name: String,
    @SerializedName("SHORT_NAME") val shortName: String
)