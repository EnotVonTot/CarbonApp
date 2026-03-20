package com.example.carbonlk.data

object MockRepository {
    var currentUser = User(
        firstName = "Никита",
        middleName = "Алексеевич",
        lastName = "Шурманов",
        phone = "+79222428165",
        email = "n.shurmanov@carbonsoft.ru",
        address = "Екатеринбург, Ленина, 1",
        accountNumber = "000464518",
        contractNumber = "464518",
        balance = "1250 р.",
        status = "Активен",
        tariff = "Белка 100",
        activationDate = "2025-05-09"
    )

    val tickets = mutableListOf(
        Ticket("1", "SUP-1668960", TicketStatus.CLOSED),
        Ticket("2", "SUP-1667543", TicketStatus.CLOSED),
        Ticket("3", "SUP-1650042", TicketStatus.OPEN)
    )

    fun addTicket(subject: String, description: String): Ticket {
        val newNumber = "SUP-${System.currentTimeMillis()}".takeLast(12)
        val newTicket = Ticket(
            id = (tickets.size + 1).toString(),
            number = newNumber,
            status = TicketStatus.OPEN,
            subject = subject,
            description = description
        )
        tickets.add(0, newTicket)
        return newTicket
    }
}