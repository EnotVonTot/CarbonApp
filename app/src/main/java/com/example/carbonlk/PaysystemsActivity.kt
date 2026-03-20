package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityPaysystemsBinding

class PaysystemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPaysystemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityPaysystemsBinding.inflate(layoutInflater)
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
            startActivity(Intent(this, ServicesActivity::class.java))
            finish()
        }

        binding.navPayment.setOnClickListener {
            Toast.makeText(this, "Оплата", Toast.LENGTH_SHORT).show()
        }

        binding.navSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
            finish()
        }

        // Кнопки оплаты
        binding.unitellerPayButton.setOnClickListener {
            val amount = binding.unitellerAmount.text.toString()
            val phone = binding.unitellerPhone.text.toString()
            val email = binding.unitellerEmail.text.toString()

            if (validatePaymentFields(amount, phone, email)) {
                Toast.makeText(this, "Оплата через Uniteller: $amount руб.", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tbankPayButton.setOnClickListener {
            val amount = binding.tbankAmount.text.toString()
            val phone = binding.tbankPhone.text.toString()
            val email = binding.tbankEmail.text.toString()

            if (validatePaymentFields(amount, phone, email)) {
                Toast.makeText(this, "Оплата через Т-банк: $amount руб.", Toast.LENGTH_SHORT).show()
            }
        }

        // Переход на личную страницу
        binding.greetingTextView.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun validatePaymentFields(amount: String, phone: String, email: String): Boolean {
        return when {
            amount.isEmpty() -> {
                Toast.makeText(this, "Введите сумму", Toast.LENGTH_SHORT).show()
                false
            }
            phone.isEmpty() -> {
                Toast.makeText(this, "Введите телефон", Toast.LENGTH_SHORT).show()
                false
            }
            email.isEmpty() -> {
                Toast.makeText(this, "Введите email", Toast.LENGTH_SHORT).show()
                false
            }
            else -> true
        }
    }
}