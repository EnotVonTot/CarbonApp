package com.example.carbonlk

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carbonlk.databinding.ActivitySettingsBinding
import com.example.carbonlk.utils.SettingsManager  // ← ВАЖНО: импорт SettingsManager

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var settingsManager: SettingsManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsManager = SettingsManager(this)

        loadSettings()
        setupClickListeners()
    }

    private fun loadSettings() {
        val currentUrl = settingsManager.getApiBaseUrl()
        binding.serverUrlInput.setText(currentUrl)
    }

    private fun setupClickListeners() {
        binding.saveServerButton.setOnClickListener {
            val newUrl = binding.serverUrlInput.text.toString().trim()
            if (newUrl.isNotEmpty()) {
                settingsManager.saveApiBaseUrl(newUrl)
                Toast.makeText(this, "Адрес сервера сохранён", Toast.LENGTH_SHORT).show()

                // Показываем статус
                binding.connectionStatus.text = "Сервер: $newUrl"
                binding.connectionStatus.visibility = android.view.View.VISIBLE
            } else {
                Toast.makeText(this, "Введите адрес сервера", Toast.LENGTH_SHORT).show()
            }
        }

        binding.backButton.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }
    }
}