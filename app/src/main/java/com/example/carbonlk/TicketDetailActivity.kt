package com.example.carbonlk

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.carbonlk.databinding.ActivityTicketDetailBinding
import com.example.carbonlk.model.CommentResponse
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TicketDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTicketDetailBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    private var ticketId: String = ""
    private var ticketNumber: String = ""
    private var ticketSubject: String = ""
    private var ticketText: String = ""
    private var ticketStatus: String = ""
    private var ticketDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        apiService = RetrofitClient.getInstance()

        binding = ActivityTicketDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadIntentData()
        setupClickListeners()
        loadComments()
    }

    private fun loadIntentData() {
        ticketId = intent.getStringExtra("ticket_id") ?: ""
        ticketNumber = intent.getStringExtra("ticket_number") ?: ""
        ticketSubject = intent.getStringExtra("ticket_subject") ?: ""
        ticketText = intent.getStringExtra("ticket_text") ?: ""
        ticketStatus = intent.getStringExtra("ticket_status") ?: ""
        ticketDate = intent.getStringExtra("ticket_date") ?: ""

        binding.ticketNumber.text = "Заявка #$ticketNumber"
        binding.ticketStatus.text = ticketStatus
        binding.ticketSubject.text = ticketSubject
        binding.ticketDescription.text = ticketText
        binding.createdDate.text = formatDate(ticketDate)

        if (ticketStatus == "Ожидает ответа") {
            binding.ticketStatus.setTextColor(getColor(R.color.white))
            binding.ticketStatus.setBackgroundResource(R.drawable.status_background_orange)
        } else {
            binding.ticketStatus.setTextColor(getColor(R.color.dark_gray))
            binding.ticketStatus.setBackgroundResource(R.drawable.status_background_gray)
        }
    }

    private fun loadComments() {
        if (ticketId.isEmpty()) {
            Toast.makeText(this, "Ошибка: ID заявки не найден", Toast.LENGTH_SHORT).show()
            return
        }

        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val argJson = "{\"suid\":\"$sessionId\",\"parent_id\":$ticketId}"

        lifecycleScope.launch {
            try {
                val response = apiService.getHelpdeskComments(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.get_helpdesk_comments",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val comments = response.body()!!
                    displayComments(comments)
                } else {
                    displayNoComments()
                }
            } catch (e: Exception) {
                displayNoComments()
            }
        }
    }

    private fun displayComments(comments: List<CommentResponse>) {
        val commentsContainer = binding.commentsContainer
        commentsContainer.removeAllViews()

        if (comments.isEmpty()) {
            displayNoComments()
            return
        }

        addTicketMessage()

        comments.forEach { comment ->
            val commentView = createCommentView(comment)
            commentsContainer.addView(commentView)
        }
    }

    private fun addTicketMessage() {
        val messageView = createMessageView(
            creator = "Вы",
            text = ticketText,
            date = formatDate(ticketDate),
            isUserMessage = true
        )
        binding.commentsContainer.addView(messageView)
    }

    private fun createCommentView(comment: CommentResponse): View {
        val isUserComment = comment.creatorName == "root"
        return createMessageView(
            creator = if (isUserComment) "Поддержка" else comment.creatorName,
            text = comment.text,
            date = formatDate(comment.createDate),
            isUserMessage = !isUserComment
        )
    }

    private fun createMessageView(creator: String, text: String, date: String, isUserMessage: Boolean): View {
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 0, 0, 16)
            }
            setPadding(16, 12, 16, 12)
            setBackgroundResource(
                if (isUserMessage) R.drawable.bg_message_user
                else R.drawable.bg_message_support
            )
        }

        // Имя отправителя
        val creatorTextView = TextView(this)
        creatorTextView.text = creator
        creatorTextView.textSize = 12f
        creatorTextView.setTypeface(null, android.graphics.Typeface.BOLD)
        creatorTextView.setTextColor(getColor(if (isUserMessage) R.color.purple_500 else R.color.green))
        layout.addView(creatorTextView)

        // Текст сообщения
        val textTextView = TextView(this)
        textTextView.text = text
        textTextView.textSize = 14f
        textTextView.setTextColor(getColor(R.color.black))
        textTextView.setPadding(0, 4, 0, 4)
        layout.addView(textTextView)

        // Дата
        val dateTextView = TextView(this)
        dateTextView.text = date
        dateTextView.textSize = 10f
        dateTextView.setTextColor(getColor(R.color.dark_gray))
        layout.addView(dateTextView)

        return layout
    }

    private fun displayNoComments() {
        val commentsContainer = binding.commentsContainer
        commentsContainer.removeAllViews()

        addTicketMessage()

        val emptyText = TextView(this)
        emptyText.text = "Нет комментариев"
        emptyText.textSize = 14f
        emptyText.setTextColor(getColor(R.color.dark_gray))
        emptyText.gravity = android.view.Gravity.CENTER
        emptyText.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        emptyText.setPadding(0, 16, 0, 16)
        commentsContainer.addView(emptyText)
    }

    private fun formatDate(dateString: String): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
            val date: Date = inputFormat.parse(dateString)
            outputFormat.format(date)
        } catch (e: Exception) {
            dateString
        }
    }

    private fun setupClickListeners() {
        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}