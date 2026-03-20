package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityServicesBinding

class ServicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
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

            binding.bottomNavigation.setPadding(
                0,
                0,
                0,
                systemInsets.bottom
            )

            insets
        }
    }

    private fun setupClickListeners() {
        // Навигация
        binding.navHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        binding.navServices.setOnClickListener {
            Toast.makeText(this, "Услуги", Toast.LENGTH_SHORT).show()
        }

        binding.navPayment.setOnClickListener {
            startActivity(Intent(this, PaysystemsActivity::class.java))
            finish()
        }

        binding.navSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
            finish()
        }

        // Кнопки подключения
        binding.parentalControlButton.setOnClickListener {
            Toast.makeText(this, "Подключение родительского контроля", Toast.LENGTH_SHORT).show()
        }

        binding.laikaConnectButton.setOnClickListener {
            Toast.makeText(this, "Подключение Лайка 300", Toast.LENGTH_SHORT).show()
        }

        binding.medvedConnectButton.setOnClickListener {
            Toast.makeText(this, "Подключение Медведь 1000", Toast.LENGTH_SHORT).show()
        }

        binding.camerasConnectButton.setOnClickListener {
            Toast.makeText(this, "Подключение Видеокамер", Toast.LENGTH_SHORT).show()
        }

        binding.turboConnectButton.setOnClickListener {
            Toast.makeText(this, "Подключение Турбокнопки", Toast.LENGTH_SHORT).show()
        }

        binding.iptvConnectButton.setOnClickListener {
            Toast.makeText(this, "Подключение IPTV 24TV", Toast.LENGTH_SHORT).show()
        }

        // Переход на личную страницу
        binding.greetingTextView.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}