package com.example.carbonlk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carbonlk.databinding.ActivityAccountEditBinding
import com.example.carbonlk.data.MockRepository
import com.example.carbonlk.utils.SessionManager
import java.util.regex.Pattern

class AccountEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountEditBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val userData = sessionManager.getUserData()
        val phone = userData?.user?.abonent?.sms ?: ""
        binding.phoneInput.setText(phone)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val phone = binding.phoneInput.text.toString().trim()

            if (!validatePhone(phone)) {
                binding.phoneLayout.error = "Введите корректный номер телефона (только +, цифры, пробелы, дефисы)"
                return@setOnClickListener
            }

            saveUserData(phone)
        }
    }

    private fun validatePhone(phone: String): Boolean {
        val pattern = Pattern.compile("^[+\\d\\s\\-]{10,20}$")
        return pattern.matcher(phone).matches()
    }

    private fun saveUserData(phone: String) {
        // TODO: Здесь будет вызов API для обновления телефона
        Toast.makeText(this, "Телефон сохранён: $phone", Toast.LENGTH_SHORT).show()
        finish()
    }
}