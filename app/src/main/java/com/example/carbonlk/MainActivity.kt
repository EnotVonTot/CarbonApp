package com.example.carbonlk

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.carbonlk.databinding.ActivityMainBinding
import com.example.carbonlk.model.ExpenseItem
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)
        apiService = RetrofitClient.getInstance()

        if (!sessionManager.isLoggedIn()) {
            navigateToLogin()
            return
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        setupClickListeners()
        setupBlockingDatePicker()

        val userData = sessionManager.getUserData()
        if (userData == null) {
            loadUserDataFromApi()
        } else {
            displayUserData(userData)
        }

        // Загружаем расходы
        loadExpenses()
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

    private fun loadUserDataFromApi() {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            navigateToLogin()
            return
        }

        val argJson = "{\"suid\":\"$sessionId\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.getUser(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.get_user",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val userData = response.body()
                    sessionManager.saveUserData(userData!!)
                    displayUserData(userData)
                } else {
                    showPlaceholders()
                }
            } catch (e: Exception) {
                showPlaceholders()
            }
        }
    }

    private fun displayUserData(userData: com.example.carbonlk.model.FullUserResponse) {
        // ФИО
        val fullName = userData.user?.abonent?.name
            ?: userData.user?.abonentShort
            ?: "Пользователь"
        val firstName = fullName.split(" ").firstOrNull() ?: fullName
        binding.greetingTextView.text = "Здравствуйте, $firstName"

        // Лицевой счёт и баланс
        val accountInfo = userData.user?.abonent?.accountInfo ?: ""
        if (accountInfo.isNotEmpty()) {
            val accountNumber = accountInfo.substringAfter("№ ").substringBefore(" Баланс:").trim()
            val balance = accountInfo.substringAfter("Баланс: ").trim()
            binding.accountNumberValue.text = accountNumber
            binding.balanceValue.text = "$balance р."
        } else {
            binding.accountNumberValue.text = "---"
            binding.balanceValue.text = "--- р."
        }

        // Номер договора
        binding.contractValue.text = userData.user?.abonent?.contractNumber ?: "---"

        // Статус
        val isEnabled = userData.user?.enabled == "1"
        binding.statusValue.text = if (isEnabled) "Активен" else "Неактивен"
        binding.statusValue.setTextColor(getColor(if (isEnabled) R.color.green else R.color.dark_gray))

        // Тариф
        binding.tariffValue.text = userData.user?.abonent?.tariff ?: "---"

        // Дата активации
        val fullDate = userData.user?.abonent?.createDateSystem ?: ""
        val activationDate = fullDate.split(" ").firstOrNull() ?: "---"
        binding.activationDateValue.text = activationDate

        // Личные данные
        val abonent = userData.user?.abonent
        val nameParts = fullName.split(" ")
        val lastName = nameParts.getOrNull(0) ?: ""
        val nameFirst = nameParts.getOrNull(1) ?: ""
        val patronymic = nameParts.getOrNull(2) ?: ""
        binding.userName.text = "$lastName $nameFirst $patronymic".trim()

        val phone = abonent?.sms ?: ""
        binding.userPhone.text = if (phone.isNotEmpty()) phone else "Не указан"

        val email = abonent?.email ?: ""
        binding.userEmail.text = if (email.isNotEmpty()) email else "Не указан"

        val homeAddress = abonent?.home ?: ""
        val apartmentNumber = abonent?.aHomeNumber ?: ""
        val address = when {
            homeAddress.isNotEmpty() && apartmentNumber.isNotEmpty() -> "$homeAddress, кв. $apartmentNumber"
            homeAddress.isNotEmpty() -> homeAddress
            apartmentNumber.isNotEmpty() -> "кв. $apartmentNumber"
            else -> "Не указан"
        }
        binding.userAddress.text = address
    }

    private fun loadExpenses() {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) return

        val argJson = "{\"filter\":\"\",\"get_user_uslugas_all\":true,\"get_user_uslugas\":true,\"get_setted\":false,\"suid\":\"$sessionId\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.getServicesList(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.get_usluga_list",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!
                    val expenses = parseExpenses(data)
                    displayExpenses(expenses)
                }
            } catch (e: Exception) {
                // Показываем заглушки
                val expensesContainer = binding.expensesContainer
                while (expensesContainer.childCount > 1) {
                    expensesContainer.removeViewAt(expensesContainer.childCount - 1)
                }
                val emptyText = TextView(this@MainActivity).apply {
                    text = "Нет данных о расходах"
                    textSize = 14f
                    setTextColor(getColor(R.color.dark_gray))
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    setPadding(0, 8, 0, 0)
                }
                expensesContainer.addView(emptyText)
            }
        }
    }

    private fun parseExpenses(data: List<List<Map<String, Any>>>): List<ExpenseItem> {
        val expenses = mutableListOf<ExpenseItem>()

        // data[1] - услуги, подключенные к абоненту
        if (data.size > 1) {
            data[1].forEach { serviceMap ->
                val serviceName = serviceMap["__usluga"]?.toString() ?: ""
                val debit = serviceMap["debit"]?.toString()?.toDoubleOrNull() ?: 0.0

                if (serviceName.isNotEmpty() && debit > 0) {
                    // Очищаем название от лишней информации (оставляем только название услуги)
                    val cleanName = serviceName.split(":").first().trim()
                    expenses.add(ExpenseItem(cleanName, debit))
                }
            }
        }

        return expenses
    }

    private fun displayExpenses(expenses: List<ExpenseItem>) {
        val expensesContainer = binding.expensesContainer
        // Удаляем все дочерние элементы, кроме заголовка (первого)
        while (expensesContainer.childCount > 1) {
            expensesContainer.removeViewAt(expensesContainer.childCount - 1)
        }

        if (expenses.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "Нет расходов в текущем месяце"
                textSize = 14f
                setTextColor(getColor(R.color.dark_gray))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 8, 0, 0)
            }
            expensesContainer.addView(emptyText)
            return
        }

        expenses.forEach { expense ->
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 8)
            }

            // Название услуги
            val nameTextView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
                text = expense.name
                textSize = 14f
                setTextColor(getColor(R.color.black))
            }

            // Сумма
            val amountTextView = TextView(this).apply {
                text = "${String.format("%.2f", expense.amount)} р."
                textSize = 14f
                setTextColor(getColor(R.color.black))
                setTypeface(null, android.graphics.Typeface.BOLD)
            }

            itemLayout.addView(nameTextView)
            itemLayout.addView(amountTextView)
            expensesContainer.addView(itemLayout)
        }
    }

    private fun showPlaceholders() {
        binding.greetingTextView.text = "Здравствуйте, Пользователь"
        binding.accountNumberValue.text = "---"
        binding.contractValue.text = "---"
        binding.balanceValue.text = "--- р."
        binding.statusValue.text = "Активен"
        binding.tariffValue.text = "---"
        binding.activationDateValue.text = "---"

        binding.userName.text = "---"
        binding.userPhone.text = "---"
        binding.userEmail.text = "---"
        binding.userAddress.text = "---"

        // Очищаем расходы
        val expensesContainer = binding.expensesContainer
        while (expensesContainer.childCount > 1) {
            expensesContainer.removeViewAt(expensesContainer.childCount - 1)
        }
        val emptyText = TextView(this).apply {
            text = "Нет данных о расходах"
            textSize = 14f
            setTextColor(getColor(R.color.dark_gray))
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setPadding(0, 8, 0, 0)
        }
        expensesContainer.addView(emptyText)
    }

    // ==================== Блокировка ====================

    private fun setupBlockingDatePicker() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
        val calendar = Calendar.getInstance()

        binding.blockingCard.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    val selectedDate = Calendar.getInstance().apply {
                        set(year, month, dayOfMonth)
                    }

                    if (selectedDate.timeInMillis > System.currentTimeMillis()) {
                        showBlockingDurationDialog(selectedDate, dateFormat)
                    } else {
                        Toast.makeText(this, "Выберите дату в будущем", Toast.LENGTH_SHORT).show()
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePickerDialog.datePicker.minDate = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            datePickerDialog.show()
        }
    }

    private fun showBlockingDurationDialog(startDate: Calendar, dateFormat: SimpleDateFormat) {
        val options = arrayOf("1 день", "3 дня", "7 дней", "14 дней", "30 дней")

        AlertDialog.Builder(this)
            .setTitle("Выберите длительность блокировки")
            .setItems(options) { _, which ->
                val days = when (which) {
                    0 -> 1
                    1 -> 3
                    2 -> 7
                    3 -> 14
                    4 -> 30
                    else -> 7
                }

                val endDate = Calendar.getInstance().apply {
                    time = startDate.time
                    add(Calendar.DAY_OF_YEAR, days)
                }

                val formattedStart = dateFormat.format(startDate.time)
                val formattedEnd = dateFormat.format(endDate.time)

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Подтверждение блокировки")
                    .setMessage("Вы действительно хотите заблокировать услуги с $formattedStart по $formattedEnd?")
                    .setPositiveButton("Да") { _, _ ->
                        performBlocking(startDate, endDate)
                    }
                    .setNegativeButton("Нет", null)
                    .show()
            }
            .show()
    }

    private fun performBlocking(startDate: Calendar, endDate: Calendar) {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale("ru"))
        val startDateStr = dateFormat.format(startDate.time)
        val endDateStr = dateFormat.format(endDate.time)

        val argJson = "{\"start_date\":\"$startDateStr\",\"end_date\":\"$endDateStr\",\"suid\":\"$sessionId\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.blockUser(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.block_user",
                    args = argJson
                )

                if (response.isSuccessful) {
                    val blockResponse = response.body()
                    val message = blockResponse?.message ?: "Блокировка установлена"

                    Toast.makeText(this@MainActivity, message, Toast.LENGTH_LONG).show()
                    binding.blockingDate.text = "$startDateStr – $endDateStr"
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@MainActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    // ==================== Навигация ====================

    private fun setupClickListeners() {
        binding.navHome.setOnClickListener {
            Toast.makeText(this, "Главная", Toast.LENGTH_SHORT).show()
        }

        binding.navServices.setOnClickListener {
            val intent = Intent(this, ServicesActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.navPayment.setOnClickListener {
            val intent = Intent(this, PaysystemsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.navSupport.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.editButton.setOnClickListener {
            val intent = Intent(this, AccountEditActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }

        binding.settingsCard.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
        }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
        overridePendingTransition(0, 0)
    }
}