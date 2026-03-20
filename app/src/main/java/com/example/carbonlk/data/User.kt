package com.example.carbonlk.data

data class User(
    val firstName: String = "Никита",
    val lastName: String = "Шурманов",
    val phone: String = "+79222428165",
    val email: String = "n.shurmanov@mail.ru",
    val address: String = "Екатеринбург, Ленина, 1",
    val accountNumber: String = "000464518",
    val contractNumber: String = "464518",
    val balance: String = "1250 р.",
    val status: String = "Активен",
    val tariff: String = "Белка 100",
    val activationDate: String = "2025-05-09"
)