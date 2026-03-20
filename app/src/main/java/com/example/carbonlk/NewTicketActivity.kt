package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityNewTicketBinding
import com.example.carbonlk.data.MockRepository

class NewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge режим
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Обработка выреза камеры
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityNewTicketBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupClickListeners()
        loadUserData()
    }

    private fun applyWindowInsets() {
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            val systemInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
                .getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemInsets.left,
                systemInsets.top,
                systemInsets.right,
                systemInsets.bottom
            )

            insets
        }
    }

    private fun loadUserData() {
        val user = MockRepository.currentUser
        binding.greetingTextView.text = "Здравствуйте, ${user.firstName}"
    }

    private fun setupClickListeners() {
        // Сохранение заявки
        binding.saveTicketButton.setOnClickListener {
            val subject = binding.ticketSubject.text.toString().trim()
            val description = binding.ticketDescription.text.toString().trim()

            if (subject.isEmpty()) {
                binding.ticketSubjectLayout.error = "Введите тему заявки"
                return@setOnClickListener
            } else {
                binding.ticketSubjectLayout.error = null
            }

            if (description.isEmpty()) {
                binding.ticketDescriptionLayout.error = "Введите описание заявки"
                return@setOnClickListener
            } else {
                binding.ticketDescriptionLayout.error = null
            }

            // TODO: Здесь будет вызов API для создания заявки
            // Пока используем MockRepository
            val newTicket = MockRepository.addTicket(subject, description)
            Toast.makeText(this, "Заявка ${newTicket.number} создана", Toast.LENGTH_SHORT).show()
            finish()
        }

        // Кнопка Назад (возврат на экран поддержки)
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        // Переход на личную страницу (по клику на приветствие)
        binding.greetingTextView.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }
}