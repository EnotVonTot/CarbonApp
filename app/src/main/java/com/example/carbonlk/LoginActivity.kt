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
import com.example.carbonlk.databinding.ActivityLoginBinding
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        apiService = RetrofitClient.getInstance()

        // Проверяем, есть ли уже активная сессия
        if (sessionManager.isLoggedIn()) {
            navigateToMain()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        prefillDemoData()
        setupClickListeners()
    }

    private fun applyWindowInsets() {
        binding.main.setOnApplyWindowInsetsListener { view, insets ->
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

    private fun prefillDemoData() {
        binding.loginInput.setText("testapi")
        binding.passwordInput.setText("servicemode")
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val login = binding.loginInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            if (!validateLogin(login) || !validatePassword(password)) {
                return@setOnClickListener
            }

            performLogin(login, password)
        }

        binding.forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Демо-доступ: testapi / servicemode", Toast.LENGTH_LONG).show()
        }
    }

    private fun performLogin(login: String, password: String) {
        binding.loginButton.isEnabled = false
        binding.loginButton.text = "Вход..."

        val argJson = "{\"login\":\"$login\",\"passwd\":\"$password\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.login(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.login",
                    args = argJson
                )

                if (response.isSuccessful) {
                    val loginData = response.body()
                    if (loginData != null && loginData.sessionId.isNotEmpty()) {
                        sessionManager.saveSession(
                            sessionId = loginData.sessionId,
                            login = login,
                            fullName = login
                        )
                        loadUserFullData(loginData.sessionId, login)
                    } else {
                        Toast.makeText(
                            this@LoginActivity,
                            "Ошибка авторизации: неверный логин или пароль",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Войти"
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка сервера: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.loginButton.isEnabled = true
                    binding.loginButton.text = "Войти"
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Ошибка сети: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                binding.loginButton.isEnabled = true
                binding.loginButton.text = "Войти"
            }
        }
    }

    private fun loadUserFullData(sessionId: String, login: String) {
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

                    val fullName = userData.user?.abonent?.name ?: login
                    sessionManager.saveUserFullName(fullName)

                    Toast.makeText(
                        this@LoginActivity,
                        "Успешный вход!",
                        Toast.LENGTH_SHORT
                    ).show()

                    navigateToMain()
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка загрузки данных пользователя",
                        Toast.LENGTH_LONG
                    ).show()
                    navigateToMain()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@LoginActivity,
                    "Ошибка загрузки данных: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                navigateToMain()
            } finally {
                binding.loginButton.isEnabled = true
                binding.loginButton.text = "Войти"
            }
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }

    private fun validateLogin(login: String): Boolean {
        return when {
            login.isEmpty() -> {
                binding.loginLayout.error = "Введите логин"
                false
            }
            login.length < 3 -> {
                binding.loginLayout.error = "Логин должен содержать минимум 3 символа"
                false
            }
            !Pattern.matches("^[a-zA-Z0-9]+$", login) -> {
                binding.loginLayout.error = "Только латиница и цифры"
                false
            }
            else -> {
                binding.loginLayout.error = null
                true
            }
        }
    }

    private fun validatePassword(password: String): Boolean {
        return when {
            password.isEmpty() -> {
                binding.passwordLayout.error = "Введите пароль"
                false
            }
            password.length < 4 -> {
                binding.passwordLayout.error = "Пароль должен быть минимум 4 символа"
                false
            }
            else -> {
                binding.passwordLayout.error = null
                true
            }
        }
    }
}