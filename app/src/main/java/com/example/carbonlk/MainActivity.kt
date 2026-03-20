package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityMainBinding
import com.example.carbonlk.data.MockRepository

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        displayUserData()
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
            binding.bottomNavigation.setPadding(0, 0, 0, systemInsets.bottom)
            insets
        }
    }

    private fun displayUserData() {
        val user = MockRepository.currentUser
        binding.greetingTextView.text = "Здравствуйте, ${user.firstName}"
        binding.accountNumberValue.text = user.accountNumber
        binding.contractValue.text = user.contractNumber
        binding.balanceValue.text = user.balance
        binding.statusValue.text = user.status
        binding.tariffValue.text = user.tariff
        binding.activationDateValue.text = user.activationDate
    }

    private fun setupClickListeners() {
        // Навигация по нижним кнопкам
        binding.navHome.setOnClickListener {
            Toast.makeText(this, "Главная", Toast.LENGTH_SHORT).show()
        }

        binding.navServices.setOnClickListener {
            startActivity(Intent(this, ServicesActivity::class.java))
        }

        binding.navPayment.setOnClickListener {
            startActivity(Intent(this, PaysystemsActivity::class.java))
        }

        binding.navSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
        }

        // Переход на личную страницу
        binding.greetingTextView.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }

        // Обработка кнопки "Взять" для обещанного платежа
        binding.buttonTake.setOnClickListener {
            val selectedDays = when (binding.paymentRadioGroup.checkedRadioButtonId) {
                R.id.radio3days -> 3
                R.id.radio7days -> 7
                else -> 3
            }
            Toast.makeText(this, "Обещанный платёж на $selectedDays дня(ей)", Toast.LENGTH_SHORT).show()
        }
    }
}