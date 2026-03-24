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

        android.util.Log.d("LOGIN_DEBUG", "LoginActivity onCreate START")

        sessionManager = SessionManager(this)
        apiService = RetrofitClient.getInstance()

        // Проверяем, есть ли уже активная сессия
        if (sessionManager.isLoggedIn()) {
            android.util.Log.d("LOGIN_DEBUG", "Already logged in, navigating to Main")
            navigateToMain()
            return
        }

        android.util.Log.d("LOGIN_DEBUG", "Not logged in, setting up UI")

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

        android.util.Log.d("LOGIN_DEBUG", "LoginActivity onCreate END")
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

            android.util.Log.d("LOGIN_DEBUG", "Login button clicked: $login")

            if (!validateLogin(login) || !validatePassword(password)) {
                android.util.Log.d("LOGIN_DEBUG", "Validation failed")
                return@setOnClickListener
            }

            performLogin(login, password)
        }

        binding.forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Демо-доступ: testapi / servicemode", Toast.LENGTH_LONG).show()
        }
    }

    private fun performLogin(login: String, password: String) {
        android.util.Log.d("LOGIN_DEBUG", "performLogin called for: $login")

        binding.loginButton.isEnabled = false
        binding.loginButton.text = "Вход..."

        val argJson = "{\"login\":\"$login\",\"passwd\":\"$password\"}"
        android.util.Log.d("LOGIN_DEBUG", "argJson: $argJson")

        lifecycleScope.launch {
            try {
                android.util.Log.d("LOGIN_DEBUG", "Calling login API...")
                val response = apiService.login(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.login",
                    args = argJson
                )

                android.util.Log.d("LOGIN_DEBUG", "Login response code: ${response.code()}")

                if (response.isSuccessful) {
                    val loginData = response.body()
                    android.util.Log.d("LOGIN_DEBUG", "Login response body: $loginData")

                    if (loginData != null && loginData.sessionId.isNotEmpty()) {
                        android.util.Log.d("LOGIN_DEBUG", "Session ID: ${loginData.sessionId}")

                        sessionManager.saveSession(
                            sessionId = loginData.sessionId,
                            login = login,
                            fullName = login
                        )

                        android.util.Log.d("LOGIN_DEBUG", "Calling loadUserFullData...")
                        loadUserFullData(loginData.sessionId, login)
                    } else {
                        android.util.Log.e("LOGIN_DEBUG", "No sessionId in response")
                        Toast.makeText(
                            this@LoginActivity,
                            "Ошибка авторизации: неверный логин или пароль",
                            Toast.LENGTH_LONG
                        ).show()
                        binding.loginButton.isEnabled = true
                        binding.loginButton.text = "Войти"
                    }
                } else {
                    android.util.Log.e("LOGIN_DEBUG", "Login failed with code: ${response.code()}")
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка сервера: ${response.code()}",
                        Toast.LENGTH_LONG
                    ).show()
                    binding.loginButton.isEnabled = true
                    binding.loginButton.text = "Войти"
                }
            } catch (e: Exception) {
                android.util.Log.e("LOGIN_DEBUG", "Login exception: ${e.message}", e)
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
        android.util.Log.d("LOGIN_DEBUG", "loadUserFullData START, sessionId: $sessionId")

        val argJson = "{\"suid\":\"$sessionId\"}"
        android.util.Log.d("LOGIN_DEBUG", "getUser argJson: $argJson")

        lifecycleScope.launch {
            try {
                android.util.Log.d("LOGIN_DEBUG", "Calling getUser API...")
                val response = apiService.getUser(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.get_user",
                    args = argJson
                )

                android.util.Log.d("LOGIN_DEBUG", "getUser response code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val userData = response.body()
                    android.util.Log.d("LOGIN_DEBUG", "=== USER DATA RECEIVED ===")
                    android.util.Log.d("LOGIN_DEBUG", "abonent.name: ${userData?.user?.abonent?.name}")
                    android.util.Log.d("LOGIN_DEBUG", "abonent.sms: ${userData?.user?.abonent?.sms}")
                    android.util.Log.d("LOGIN_DEBUG", "abonent.email: ${userData?.user?.abonent?.email}")
                    android.util.Log.d("LOGIN_DEBUG", "abonent.__home: ${userData?.user?.abonent?.home}")

                    sessionManager.saveUserData(userData!!)
                    android.util.Log.d("LOGIN_DEBUG", "User data saved to SessionManager")

                    val fullName = userData.user?.abonent?.name ?: login
                    sessionManager.saveUserFullName(fullName)

                    android.util.Log.d("LOGIN_DEBUG", "About to call navigateToMain")

                    // Разблокируем кнопку
                    binding.loginButton.isEnabled = true
                    binding.loginButton.text = "Войти"

                    // Переход на MainActivity
                    navigateToMain()
                } else {
                    android.util.Log.e("LOGIN_DEBUG", "getUser failed or body is null")
                    binding.loginButton.isEnabled = true
                    binding.loginButton.text = "Войти"
                    Toast.makeText(
                        this@LoginActivity,
                        "Ошибка загрузки данных пользователя",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("LOGIN_DEBUG", "getUser exception: ${e.message}", e)
                binding.loginButton.isEnabled = true
                binding.loginButton.text = "Войти"
                Toast.makeText(
                    this@LoginActivity,
                    "Ошибка загрузки данных: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToMain() {
        android.util.Log.d("LOGIN_DEBUG", "navigateToMain CALLED")
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        android.util.Log.d("LOGIN_DEBUG", "MainActivity started via startActivity")
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