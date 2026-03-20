package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityLoginBinding
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    // Демо-данные для входа
    private val validCredentials = mapOf(
        "demo" to "demo123",
        "user" to "password",
        "admin" to "admin123"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge и обработка выреза камеры
        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()

        // Автоматически заполняем демо-данными
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
        // Заполняем поля демо-данными
        binding.loginInput.setText("demo")
        binding.passwordInput.setText("demo123")
    }

    private fun setupClickListeners() {
        binding.loginButton.setOnClickListener {
            val login = binding.loginInput.text.toString().trim()
            val password = binding.passwordInput.text.toString()

            if (!validateLogin(login) || !validatePassword(password)) {
                return@setOnClickListener
            }

            if (checkCredentials(login, password)) {
                Toast.makeText(this, "Успешный вход!", Toast.LENGTH_SHORT).show()

                // Небольшая задержка для красоты
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }, 500)
            } else {
                Toast.makeText(this, "Неверный логин или пароль", Toast.LENGTH_LONG).show()
            }
        }

        binding.forgotPasswordTextView.setOnClickListener {
            Toast.makeText(this, "Демо-доступ: demo/demo123", Toast.LENGTH_LONG).show()
        }
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

    private fun checkCredentials(login: String, password: String): Boolean {
        return validCredentials[login] == password
    }
}