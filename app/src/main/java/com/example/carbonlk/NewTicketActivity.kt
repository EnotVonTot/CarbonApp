package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.carbonlk.databinding.ActivityNewTicketBinding
import com.example.carbonlk.data.MockRepository
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import kotlinx.coroutines.launch

class NewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTicketBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        apiService = RetrofitClient.getInstance()

        // Проверяем, авторизован ли пользователь
        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

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
        val userData = sessionManager.getUserData()
        val firstName = userData?.user?.abonent?.name?.split(" ")?.firstOrNull()
            ?: sessionManager.getUserFullName()
            ?: "Пользователь"
        binding.greetingTextView.text = "Здравствуйте, $firstName"
    }

    private fun setupClickListeners() {
        // Кнопка Назад (возврат на экран поддержки)
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

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

            createTicket(subject, description)
        }
    }

    private fun createTicket(subject: String, description: String) {
        // TODO: Здесь будет вызов API для создания заявки
        // Пока используем MockRepository
        val newTicket = MockRepository.addTicket(subject, description)
        Toast.makeText(this, "Заявка ${newTicket.number} создана", Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}