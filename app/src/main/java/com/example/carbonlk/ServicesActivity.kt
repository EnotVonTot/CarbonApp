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

    private fun navigateTo(activityClass: Class<*>) {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        overridePendingTransition(0, 0)  // Отключаем анимацию
        finish()  // Закрываем текущую активность (если нужно)
    }

    private fun setupClickListeners() {
        // ... остальные слушатели

        binding.navHome.setOnClickListener {
            navigateTo(MainActivity::class.java)
        }

        binding.navPayment.setOnClickListener {
            navigateTo(PaysystemsActivity::class.java)
        }

        binding.navSupport.setOnClickListener {
            navigateTo(SupportActivity::class.java)
        }

        binding.greetingTextView.setOnClickListener {
            navigateTo(AccountActivity::class.java)
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
    }
}