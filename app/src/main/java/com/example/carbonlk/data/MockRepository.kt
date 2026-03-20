package com.example.carbonlk.data

object MockRepository {
    var currentUser = User()

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