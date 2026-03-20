package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityAccountBinding
import com.example.carbonlk.data.MockRepository

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        displayUserData()
        setupClickListeners()
        setupSwitches()
        setupBlockingDatePicker()
    }

    private fun applyWindowInsets() {
        binding.root.setOnApplyWindowInsetsListener { view, insets ->
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

    private fun displayUserData() {
        val user = MockRepository.currentUser
        // Используем метод getFullName() для отображения полного ФИО
        binding.userName.text = user.getFullName()
        binding.userPhone.text = user.phone
        binding.userEmail.text = user.email
        binding.userAddress.text = user.address
    }

    private fun setupClickListeners() {
        // Кнопка Изменить
        binding.editButton.setOnClickListener {
            val intent = Intent(this, AccountEditActivity::class.java).apply {
                // Передаём текущие данные
                putExtra("fullName", MockRepository.currentUser.firstName)
                putExtra("phone", MockRepository.currentUser.phone)
                putExtra("email", MockRepository.currentUser.email)
                putExtra("address", MockRepository.currentUser.address)
            }
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        // Кнопка Назад (возврат на MainActivity)
        binding.backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        // Блокировка
        binding.blockingCard.setOnClickListener {
            Toast.makeText(this, "Выберите дату блокировки", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupSwitches() {
        // Push
        binding.pushSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "включены" else "выключены"
            Toast.makeText(this, "Push-уведомления $status", Toast.LENGTH_SHORT).show()
        }

        // SMS
        binding.smsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "включены" else "выключены"
            Toast.makeText(this, "SMS-уведомления $status", Toast.LENGTH_SHORT).show()
        }

        // Email
        binding.emailSwitch.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "включены" else "выключены"
            Toast.makeText(this, "Email-уведомления $status", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBlockingDatePicker() {
        // Здесь код DatePicker (если уже есть)
    }
}