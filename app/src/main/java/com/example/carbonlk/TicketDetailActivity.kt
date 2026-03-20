package com.example.carbonlk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carbonlk.databinding.ActivityTicketDetailBinding
import com.example.carbonlk.data.MockRepository
import com.example.carbonlk.data.Ticket
import com.example.carbonlk.data.TicketStatus

class TicketDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketDetailBinding
    private var ticket: Ticket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadTicketData()
        setupClickListeners()
    }

    private fun loadTicketData() {
        // Получаем ID заявки из Intent
        val ticketId = intent.getStringExtra("ticket_id")
        val ticketNumber = intent.getStringExtra("ticket_number")

        // Ищем заявку в MockRepository (в будущем — запрос к API)
        ticket = if (ticketId != null) {
            MockRepository.tickets.find { it.id == ticketId }
        } else if (ticketNumber != null) {
            MockRepository.tickets.find { it.number == ticketNumber }
        } else {
            null
        }

        if (ticket == null) {
            Toast.makeText(this, "Заявка не найдена", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        displayTicketData()
    }

    private fun displayTicketData() {
        ticket?.let {
            binding.ticketNumber.text = it.number
            binding.ticketStatus.text = if (it.status == TicketStatus.OPEN) "Открыта" else "Закрыта"

            // Настраиваем цвет статуса
            if (it.status == TicketStatus.OPEN) {
                binding.ticketStatus.setTextColor(getColor(R.color.green))
                binding.ticketStatus.setBackgroundResource(R.drawable.status_background)
            } else {
                binding.ticketStatus.setTextColor(getColor(R.color.dark_gray))
                binding.ticketStatus.setBackgroundResource(R.drawable.status_background_gray)
            }

            binding.ticketSubject.text = it.subject.ifEmpty { "Без темы" }
            binding.ticketDescription.text = it.description.ifEmpty { "Нет описания" }

            // TODO: Добавить реальную дату, когда будет API
            binding.createdDate.text = "Создано: ${it.id}" // временно
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}