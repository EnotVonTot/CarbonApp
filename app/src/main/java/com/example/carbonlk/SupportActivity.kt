package com.example.carbonlk

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivitySupportBinding
import com.example.carbonlk.data.MockRepository
import com.example.carbonlk.data.TicketStatus

class SupportActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivitySupportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        displayTickets()
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

    private fun displayTickets() {
        val tickets = MockRepository.tickets

        // Обновляем видимость и содержимое карточек
        if (tickets.size > 0) {
            binding.ticket1Card.visibility = View.VISIBLE
            binding.ticket1Number.text = tickets[0].number
            binding.ticket1Status.text = if (tickets[0].status == TicketStatus.OPEN) "Открыта" else "Закрыта"
            binding.ticket1Status.setBackgroundResource(
                if (tickets[0].status == TicketStatus.OPEN) R.drawable.status_background
                else R.drawable.status_background_gray
            )
        } else {
            binding.ticket1Card.visibility = View.GONE
        }

        if (tickets.size > 1) {
            binding.ticket2Card.visibility = View.VISIBLE
            binding.ticket2Number.text = tickets[1].number
            binding.ticket2Status.text = if (tickets[1].status == TicketStatus.OPEN) "Открыта" else "Закрыта"
            binding.ticket2Status.setBackgroundResource(
                if (tickets[1].status == TicketStatus.OPEN) R.drawable.status_background
                else R.drawable.status_background_gray
            )
        } else {
            binding.ticket2Card.visibility = View.GONE
        }

        if (tickets.size > 2) {
            binding.ticket3Card.visibility = View.VISIBLE
            binding.ticket3Number.text = tickets[2].number
            binding.ticket3Status.text = if (tickets[2].status == TicketStatus.OPEN) "Открыта" else "Закрыта"
            binding.ticket3Status.setBackgroundResource(
                if (tickets[2].status == TicketStatus.OPEN) R.drawable.status_background
                else R.drawable.status_background_gray
            )
        } else {
            binding.ticket3Card.visibility = View.GONE
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

        binding.navServices.setOnClickListener {
            navigateTo(ServicesActivity::class.java)
        }

        binding.navPayment.setOnClickListener {
            navigateTo(PaysystemsActivity::class.java)
        }

        binding.greetingTextView.setOnClickListener {
            navigateTo(AccountActivity::class.java)
        }

        binding.navSupport.setOnClickListener {
            Toast.makeText(this, "Поддержка", Toast.LENGTH_SHORT).show()
        }

        // Звонок в поддержку
        binding.callSupportButton.setOnClickListener {
            Toast.makeText(this, "Звонок в техподдержку...", Toast.LENGTH_SHORT).show()
        }

        // Новая заявка
        binding.newTicketButton.setOnClickListener {
            startActivity(Intent(this, NewTicketActivity::class.java))
        }

        // Клик по карточкам
        binding.ticket1Card.setOnClickListener {
            Toast.makeText(this, "Заявка ${binding.ticket1Number.text}", Toast.LENGTH_SHORT).show()
        }

        binding.ticket2Card.setOnClickListener {
            Toast.makeText(this, "Заявка ${binding.ticket2Number.text}", Toast.LENGTH_SHORT).show()
        }

        binding.ticket3Card.setOnClickListener {
            Toast.makeText(this, "Заявка ${binding.ticket3Number.text}", Toast.LENGTH_SHORT).show()
        }
    }
}