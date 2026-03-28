package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.carbonlk.databinding.ActivitySupportBinding
import com.example.carbonlk.model.TicketItem
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import kotlinx.coroutines.launch

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    private val tickets = mutableListOf<TicketItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        apiService = RetrofitClient.getInstance()

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        loadUserData()
        setupClickListeners()
        loadTickets()
    }

    private fun applyWindowInsets() {
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
            val systemInsets = WindowInsetsCompat.toWindowInsetsCompat(insets)
                .getInsets(WindowInsetsCompat.Type.systemBars())

            view.setPadding(
                systemInsets.left,
                systemInsets.top,
                systemInsets.right,
                0
            )
            binding.bottomNavigation.setPadding(0, 0, 0, systemInsets.bottom)
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

    private fun loadTickets() {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val argJson = "{\"suid\":\"$sessionId\",\"parent_id\":null}"

        lifecycleScope.launch {
            try {
                android.util.Log.d("SUPPORT_DEBUG", "Loading tickets, sessionId: $sessionId")

                val response = apiService.getHelpdeskDialogs(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.get_helpdesk_dialogs",
                    args = argJson
                )

                android.util.Log.d("SUPPORT_DEBUG", "Response code: ${response.code()}")

                if (response.isSuccessful) {
                    val data = response.body()
                    android.util.Log.d("SUPPORT_DEBUG", "Response body: $data")

                    if (data != null) {
                        tickets.clear()
                        tickets.addAll(data.items)
                        android.util.Log.d("SUPPORT_DEBUG", "Loaded ${tickets.size} tickets")
                        displayTickets()
                    } else {
                        android.util.Log.e("SUPPORT_DEBUG", "Response body is null")
                        Toast.makeText(this@SupportActivity, "Ошибка: пустой ответ", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    android.util.Log.e("SUPPORT_DEBUG", "Error response: $errorBody")
                    Toast.makeText(this@SupportActivity, "Ошибка загрузки заявок: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("SUPPORT_DEBUG", "Exception: ${e.message}", e)
                Toast.makeText(this@SupportActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun displayTickets() {
        // Получаем контейнер для списка заявок
        val ticketsContainer = binding.ticketsContainer
        ticketsContainer.removeAllViews()

        if (tickets.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "Нет заявок"
                textSize = 14f
                setTextColor(getColor(R.color.dark_gray))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 32, 0, 32)
            }
            ticketsContainer.addView(emptyText)
            return
        }

        tickets.forEach { ticket ->
            val ticketCard = createTicketCard(ticket)
            ticketsContainer.addView(ticketCard)
        }
    }

    private fun createTicketCard(ticket: TicketItem): View {
        val cardView = androidx.cardview.widget.CardView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            radius = 16f
            cardElevation = 4f
            setCardBackgroundColor(getColor(R.color.light_gray))
            setContentPadding(16, 16, 16, 16)
            isClickable = true
            isFocusable = true

            setOnClickListener {
                val intent = Intent(this@SupportActivity, TicketDetailActivity::class.java).apply {
                    putExtra("ticket_id", ticket.id)
                    putExtra("ticket_number", ticket.pk)
                    putExtra("ticket_subject", ticket.subject)
                    putExtra("ticket_text", ticket.text)
                    putExtra("ticket_status", ticket.status)
                    putExtra("ticket_date", ticket.createdDate)
                }
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }

        // Верхняя строка: номер заявки и статус
        val topRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            gravity = android.view.Gravity.CENTER_VERTICAL
        }

        val numberTextView = TextView(this).apply {
            text = "Заявка #${ticket.pk}"
            textSize = 14f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(getColor(R.color.black))
            layoutParams = LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        val statusTextView = TextView(this).apply {
            text = ticket.status
            textSize = 12f
            setTextColor(getColor(if (ticket.status == "Ожидает ответа") R.color.white else R.color.green))
            setPadding(8, 4, 8, 4)
            background = getDrawable(
                if (ticket.status == "Ожидает ответа") R.drawable.status_background_orange
                else R.drawable.status_background_gray
            )
        }

        topRow.addView(numberTextView)
        topRow.addView(statusTextView)
        layout.addView(topRow)

        // Тема заявки
        val subjectTextView = TextView(this).apply {
            text = ticket.subject
            textSize = 16f
            setTypeface(null, android.graphics.Typeface.BOLD)
            setTextColor(getColor(R.color.black))
            setPadding(0, 8, 0, 4)
        }
        layout.addView(subjectTextView)

        // Дата создания
        val dateTextView = TextView(this).apply {
            val formattedDate = ticket.createdDate.split(".").firstOrNull() ?: ticket.createdDate
            text = formattedDate
            textSize = 12f
            setTextColor(getColor(R.color.dark_gray))
        }
        layout.addView(dateTextView)

        cardView.addView(layout)
        return cardView
    }

    private fun setupClickListeners() {
        binding.navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navServices.setOnClickListener {
            val intent = Intent(this, ServicesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navPayment.setOnClickListener {
            val intent = Intent(this, PaysystemsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navSupport.setOnClickListener {
            Toast.makeText(this, "Поддержка", Toast.LENGTH_SHORT).show()
        }

        // Новая заявка
        binding.newTicketButton.setOnClickListener {
            val intent = Intent(this, NewTicketActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}