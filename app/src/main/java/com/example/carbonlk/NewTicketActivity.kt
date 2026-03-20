package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityNewTicketBinding
import com.example.carbonlk.data.MockRepository

class NewTicketActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNewTicketBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityNewTicketBinding.inflate(layoutInflater)
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
            startActivity(Intent(this, PaysystemsActivity::class.java))
            finish()
        }

        binding.navSupport.setOnClickListener {
            startActivity(Intent(this, SupportActivity::class.java))
            finish()
        }

        // Сохранение заявки
        binding.saveTicketButton.setOnClickListener {
            val subject = binding.ticketSubject.text.toString()
            val description = binding.ticketDescription.text.toString()

            if (subject.isNotEmpty() && description.isNotEmpty()) {
                val newTicket = MockRepository.addTicket(subject, description)
                Toast.makeText(this, "Заявка ${newTicket.number} создана", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Заполните тему и описание", Toast.LENGTH_SHORT).show()
            }
        }

        // Переход на личную страницу
        binding.greetingTextView.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}