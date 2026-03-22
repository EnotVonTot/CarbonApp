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
import com.example.carbonlk.databinding.ActivityMainBinding
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupClickListeners()

        // Проверяем, есть ли данные пользователя
        val userData = sessionManager.getUserData()
        if (userData == null) {
            loadUserDataFromApi()
        } else {
            displayUserData(userData)
        }
    }

    private fun loadUserDataFromApi() {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            navigateToLogin()
            return
        }

        val argJson = "{\"suid\":\"$sessionId\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.getUser(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.get_user",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val userData = response.body()
                    sessionManager.saveUserData(userData!!)
                    displayUserData(userData)
                } else {
                    showPlaceholders()
                }
            } catch (e: Exception) {
                showPlaceholders()
            }
        }
    }

    private fun displayUserData(userData: com.example.carbonlk.model.FullUserResponse) {
        // ФИО
        val fullName = userData.user?.abonent?.name
            ?: userData.user?.abonentShort
            ?: "Пользователь"
        val firstName = fullName.split(" ").firstOrNull() ?: fullName
        binding.greetingTextView.text = "Здравствуйте, $firstName"

        // Лицевой счёт и баланс
        val accountInfo = userData.user?.abonent?.accountInfo ?: ""
        if (accountInfo.isNotEmpty()) {
            val accountNumber = accountInfo.substringAfter("№ ").substringBefore(" Баланс:").trim()
            val balance = accountInfo.substringAfter("Баланс: ").trim()
            binding.accountNumberValue.text = accountNumber
            binding.balanceValue.text = "$balance р."
        } else {
            binding.accountNumberValue.text = "---"
            binding.balanceValue.text = "--- р."
        }

        // Номер договора
        binding.contractValue.text = userData.user?.abonent?.contractNumber ?: "---"

        // Статус
        val isEnabled = userData.user?.enabled == "1"
        binding.statusValue.text = if (isEnabled) "Активен" else "Неактивен"
        binding.statusValue.setTextColor(getColor(if (isEnabled) R.color.green else R.color.dark_gray))

        // Тариф
        binding.tariffValue.text = userData.user?.abonent?.tariff ?: "---"

        // Дата активации
        val fullDate = userData.user?.abonent?.createDateSystem ?: ""
        val activationDate = fullDate.split(" ").firstOrNull() ?: "---"
        binding.activationDateValue.text = activationDate
    }

    private fun showPlaceholders() {
        binding.greetingTextView.text = "Здравствуйте, Пользователь"
        binding.accountNumberValue.text = "---"
        binding.contractValue.text = "---"
        binding.balanceValue.text = "--- р."
        binding.statusValue.text = "Активен"
        binding.tariffValue.text = "---"
        binding.activationDateValue.text = "---"
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

    private fun setupClickListeners() {
        binding.navHome.setOnClickListener {
            Toast.makeText(this, "Главная", Toast.LENGTH_SHORT).show()
        }

        binding.navServices.setOnClickListener {
            val intent = Intent(this, ServicesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.navPayment.setOnClickListener {
            val intent = Intent(this, PaysystemsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.navSupport.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.greetingTextView.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.buttonTake.setOnClickListener {
            val selectedDays = when (binding.paymentRadioGroup.checkedRadioButtonId) {
                R.id.radio3days -> 3
                R.id.radio7days -> 7
                else -> 3
            }
            Toast.makeText(this, "Обещанный платёж на $selectedDays дня(ей)", Toast.LENGTH_SHORT).show()
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