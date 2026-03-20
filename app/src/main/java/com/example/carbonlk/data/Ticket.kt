package com.example.carbonlk.data

enum class TicketStatus { OPEN, CLOSED }

data class Ticket(
    val id: String,
    val number: String,
    val status: TicketStatus,
    val subject: String = "",
    val description: String = ""
)