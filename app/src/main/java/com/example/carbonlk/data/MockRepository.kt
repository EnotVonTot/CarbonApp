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
        Ticket(
            id = "1",
            number = "SUP-1668960",
            status = TicketStatus.CLOSED,
            subject = "Проблема с интернетом",
            description = "Не работает интернет уже второй день. Перезагружал роутер - не помогло."
        ),
        Ticket(
            id = "2",
            number = "SUP-1667543",
            status = TicketStatus.CLOSED,
            subject = "Сбой в работе личного кабинета",
            description = "Не могу войти в личный кабинет, пишет 'ошибка авторизации'."
        ),
        Ticket(
            id = "3",
            number = "SUP-1650042",
            status = TicketStatus.OPEN,
            subject = "Запрос на смену тарифа",
            description = "Хочу перейти на тариф 'Белка 300'. Подскажите, как это сделать."
        )
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