package com.example.carbonlk

import android.app.DatePickerDialog
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge и обработка выреза камеры
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
        setupBlockingDatePicker()  // ВАЖНО: вызываем метод!
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
        binding.userName.text = user.getFullName()
        binding.userPhone.text = user.phone
        binding.userEmail.text = user.email
        binding.userAddress.text = user.address
    }

    private fun setupClickListeners() {
        // Кнопка Изменить
        binding.editButton.setOnClickListener {
            val intent = Intent(this, AccountEditActivity::class.java).apply {
                putExtra("fullName", MockRepository.currentUser.getFullName())
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
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale("ru"))

        binding.blockingCard.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    // Проверка: дата должна быть в будущем
                    if (selectedDate.timeInMillis > System.currentTimeMillis()) {
                        val formattedDate = dateFormat.format(selectedDate.time)
                        binding.blockingDate.text = formattedDate
                        Toast.makeText(
                            this,
                            "Блокировка установлена на $formattedDate",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        Toast.makeText(
                            this,
                            "Выберите дату в будущем",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            // Устанавливаем минимальную дату (сегодня + 1 день)
            datePickerDialog.datePicker.minDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            datePickerDialog.show()
        }
    }
}