package com.example.carbonlk.model

import com.google.gson.annotations.SerializedName

data class FullUserResponse(
    @SerializedName("error") val error: String? = null,
    @SerializedName("user") val user: UserFullData? = null,
    @SerializedName("__user") val userFull: String? = null
)

data class UserFullData(
    @SerializedName("__abonent") val abonentShort: String? = null,
    @SerializedName("login") val login: String? = null,
    @SerializedName("enabled") val enabled: String? = null,
    @SerializedName("phone") val phone: String? = null,      // может быть пустым
    @SerializedName("email") val email: String? = null,      // может быть пустым
    @SerializedName("home") val home: String? = null,        // адрес
    @SerializedName("abonent") val abonent: AbonentFullData? = null,
    @SerializedName("abonent_pay") val abonentPay: String? = null,
    @SerializedName("abonent_pay_next_date") val abonentPayNextDate: String? = null
)

data class AbonentFullData(
    @SerializedName("name") val name: String? = null,
    @SerializedName("__account") val accountInfo: String? = null,
    @SerializedName("contract_number") val contractNumber: String? = null,
    @SerializedName("__tarif") val tariff: String? = null,
    @SerializedName("create_date_system") val createDateSystem: String? = null,
    @SerializedName("create_date") val createDate: String? = null,
    @SerializedName("abon_pay_for_today") val abonPayForToday: String? = null,
    @SerializedName("sms") val sms: String? = null,           // телефон здесь!
    @SerializedName("email") val email: String? = null,       // email здесь!
    @SerializedName("__home") val home: String? = null,       // адрес здесь!
    @SerializedName("a_home_number") val aHomeNumber: String? = null, // номер квартиры
    @SerializedName("account") val account: AccountFullData? = null
)

data class AccountFullData(
    @SerializedName("__self") val accountNumber: String? = null
)