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
import com.example.carbonlk.databinding.ActivityPaysystemsBinding
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import com.example.carbonlk.model.PaymentResponse
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class PaysystemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaysystemsBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    // IP адрес, который передаётся в запросе (можно оставить статическим)
    private val srcIp = "192.168.220.150"

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

        binding = ActivityPaysystemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        loadUserData()
        setupClickListeners()
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

    private fun setupClickListeners() {
        // Навигация
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
            Toast.makeText(this, "Оплата", Toast.LENGTH_SHORT).show()
        }

        binding.navSupport.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        // Кнопка оплаты
        binding.payButton.setOnClickListener {
            val cardNumber = binding.cardNumberInput.text.toString().trim()
            val cardKey = binding.cardKeyInput.text.toString().trim()

            if (validateCardFields(cardNumber, cardKey)) {
                performPayment(cardNumber, cardKey)
            }
        }
    }

    private fun validateCardFields(cardNumber: String, cardKey: String): Boolean {
        return when {
            cardNumber.isEmpty() -> {
                binding.cardNumberLayout.error = "Введите номер или серию карты"
                false
            }
            cardKey.isEmpty() -> {
                binding.cardKeyLayout.error = "Введите секретный код карты"
                false
            }
            else -> {
                binding.cardNumberLayout.error = null
                binding.cardKeyLayout.error = null
                true
            }
        }
    }

    private fun performPayment(cardNumber: String, cardKey: String) {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        binding.payButton.isEnabled = false
        binding.payButton.text = "Обработка..."

        val argJson = "{\"suid\":\"$sessionId\",\"series_no\":\"$cardNumber\",\"card_key\":\"$cardKey\",\"src_ip\":\"$srcIp\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.addCardPayment(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.add_card_payment_operation",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val paymentResults = response.body()!!

                    if (paymentResults.isNotEmpty()) {
                        val result = paymentResults[0]
                        val message = result.text
                        val isSuccess = isSuccessMessage(result.type, message)
                        showPaymentResultDialog(message, isSuccess)

                        if (isSuccess) {
                            // Очищаем поля только при успешной оплате
                            binding.cardNumberInput.text?.clear()
                            binding.cardKeyInput.text?.clear()
                        }
                    } else {
                        showPaymentResultDialog("Платёж выполнен", true)
                        binding.cardNumberInput.text?.clear()
                        binding.cardKeyInput.text?.clear()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { parseErrorFromResponse(it) } ?: "Ошибка оплаты"
                    showPaymentResultDialog(errorMessage, false)
                }
            } catch (e: Exception) {
                showPaymentResultDialog("Ошибка сети: ${e.message}", false)
            } finally {
                binding.payButton.isEnabled = true
                binding.payButton.text = "Пополнить"
            }
        }
    }

    /**
     * Определяет, является ли сообщение успешным, по типу и тексту
     */
    private fun isSuccessMessage(type: String, message: String): Boolean {
        // Если тип "info" и текст не содержит "ошибка" или "уже была"
        return type == "info" &&
                !message.contains("ошибка", ignoreCase = true) &&
                !message.contains("уже была", ignoreCase = true)
    }

    /**
     * Парсит ошибку из ответа API
     */
    private fun parseErrorFromResponse(errorBody: String): String {
        return try {
            val json = JSONObject(errorBody)
            json.optString("error", json.optString("message", "Ошибка оплаты"))
        } catch (e: Exception) {
            "Ошибка оплаты"
        }
    }

    /**
     * Показывает диалог с результатом оплаты
     */
    private fun showPaymentResultDialog(message: String, isSuccess: Boolean) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(if (isSuccess) "Успешно" else "Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        try {
            val titleView = dialog.findViewById<android.widget.TextView>(androidx.appcompat.R.id.alertTitle)
            titleView?.setTextColor(getColor(if (isSuccess) R.color.green else R.color.red))
        } catch (e: Exception) {
            // Игнорируем
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