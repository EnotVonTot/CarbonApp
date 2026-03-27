package com.example.carbonlk

import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.carbonlk.databinding.ActivityServicesBinding
import com.example.carbonlk.model.AvailableService
import com.example.carbonlk.model.UserService
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SessionManager
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ServicesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicesBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var apiService: com.example.carbonlk.network.ApiService

    private val availableServices = mutableListOf<AvailableService>()
    private val userServices = mutableListOf<UserService>()

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

        binding = ActivityServicesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        loadUserData()
        setupClickListeners()
        loadServicesData()
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

    private fun loadUserData() {
        val userData = sessionManager.getUserData()
        val firstName = userData?.user?.abonent?.name?.split(" ")?.firstOrNull()
            ?: sessionManager.getUserFullName()
            ?: "Пользователь"
        binding.greetingTextView.text = "Здравствуйте, $firstName"
    }

    private fun loadServicesData() {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

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
                    parseServicesData(data)
                } else {
                    Toast.makeText(this@ServicesActivity, "Ошибка загрузки услуг", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@ServicesActivity, "Ошибка сети: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseServicesData(data: List<List<Map<String, Any>>>) {
        availableServices.clear()
        userServices.clear()

        // Сначала создаём карту: название услуги -> её pk (из третьего массива)
        val serviceNameToId = mutableMapOf<String, String>()

        // data[2] - описание услуг (третий массив)
        if (data.size > 2) {
            android.util.Log.d("SERVICES_DEBUG", "Parsing service descriptions, count: ${data[2].size}")
            data[2].forEach { serviceMap ->
                val servicePk = serviceMap["pk"]?.toString() ?: return@forEach
                val serviceName = serviceMap["name"]?.toString() ?: ""
                android.util.Log.d("SERVICES_DEBUG", "Service description: pk=$servicePk, name=$serviceName")
                serviceNameToId[serviceName] = servicePk
            }
        }

        // Создаём карту подключённых услуг по ID услуги
        val connectedServiceIds = mutableSetOf<String>()

        // data[1] - подключенные услуги пользователя (второй массив)
        if (data.size > 1) {
            android.util.Log.d("SERVICES_DEBUG", "Parsing user services, count: ${data[1].size}")
            data[1].forEach { serviceMap ->
                val userServicePk = serviceMap["pk"]?.toString() ?: return@forEach
                val serviceName = serviceMap["__usluga"]?.toString() ?: ""
                val enableDate = serviceMap["enable_date"]?.toString() ?: ""
                val activated = serviceMap["activated"]?.toString() ?: "1"

                // Извлекаем название услуги (до двоеточия)
                val shortServiceName = serviceName.split(":").first().trim()
                android.util.Log.d("SERVICES_DEBUG", "User service: pk=$userServicePk, shortName=$shortServiceName, fullName=$serviceName")

                // Находим ID услуги по названию
                val serviceId = serviceNameToId[shortServiceName] ?: ""
                if (serviceId.isNotEmpty()) {
                    connectedServiceIds.add(serviceId)
                    android.util.Log.d("SERVICES_DEBUG", "  -> matched to serviceId=$serviceId")
                }

                userServices.add(
                    UserService(
                        id = userServicePk,
                        serviceName = serviceName,
                        enableDate = enableDate,
                        activated = activated
                    )
                )
            }
        }

        android.util.Log.d("SERVICES_DEBUG", "connectedServiceIds after parsing: $connectedServiceIds")

        // data[0] - доступные услуги (первый массив)
        if (data.size > 0) {
            android.util.Log.d("SERVICES_DEBUG", "Parsing available services, count: ${data[0].size}")
            data[0].forEach { serviceMap ->
                val serviceId = serviceMap["pk"]?.toString() ?: return@forEach
                val name = serviceMap["name"]?.toString() ?: ""
                val fullName = serviceMap["__self"]?.toString() ?: ""
                val description = serviceMap["comments"]?.toString()
                val price = serviceMap["summa"]?.toString() ?: "0"
                val isShowWeb = serviceMap["is_show_web"]?.toString() ?: "1"
                val webAllowDisable = serviceMap["web_allow_disable"]?.toString() ?: "0"

                // Проверяем, подключена ли услуга по ID
                val isConnected = connectedServiceIds.contains(serviceId)
                android.util.Log.d("SERVICES_DEBUG", "Available service: id=$serviceId, name=$name, isConnected=$isConnected")

                if (isShowWeb == "1") {
                    availableServices.add(
                        AvailableService(
                            id = serviceId,
                            name = name,
                            fullName = fullName,
                            description = description,
                            price = price,
                            isShowWeb = isShowWeb,
                            webAllowDisable = webAllowDisable,
                            isConnected = isConnected
                        )
                    )
                }
            }
        }

        android.util.Log.d("SERVICES_DEBUG", "=== FINAL ===")
        android.util.Log.d("SERVICES_DEBUG", "Available services count: ${availableServices.size}")
        availableServices.forEach { service ->
            android.util.Log.d("SERVICES_DEBUG", "  - ${service.name} (id=${service.id}, isConnected=${service.isConnected})")
        }
        android.util.Log.d("SERVICES_DEBUG", "User services count: ${userServices.size}")

        displayServices()
    }

    private fun displayServices() {
        val quickServicesLayout = binding.quickServicesLayout
        quickServicesLayout.removeAllViews()

        availableServices.forEach { service ->
            val isConnected = service.isConnected

            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 0, 0, 24)
                tag = service.id
            }

            // Название услуги
            val nameTextView = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                text = service.name
                textSize = 16f
                setTextColor(getColor(R.color.black))
                setTypeface(null, Typeface.BOLD)
                setOnClickListener {
                    Toast.makeText(
                        this@ServicesActivity,
                        service.fullName,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            itemLayout.addView(nameTextView)

            // Кнопка (Подключить или Отключить)
            val button = MaterialButton(this).apply {
                text = if (isConnected) "Отключить" else "Подключить"
                textSize = 14f
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    84
                )
                cornerRadius = 24
                backgroundTintList = getColorStateList(
                    if (isConnected) R.color.red else R.color.purple_500
                )
                setTextColor(getColor(R.color.white))
                setPadding(0, 0, 0, 0)
                setOnClickListener {
                    if (isConnected) {
                        disconnectService(service)
                    } else {
                        connectService(service)
                    }
                }
            }
            itemLayout.addView(button)

            // Цена
            val priceText = if (service.price.toDoubleOrNull() == 0.0) {
                "Бесплатно"
            } else {
                "${service.price} руб."
            }

            val priceTextView = TextView(this).apply {
                text = priceText
                textSize = 12f
                setTextColor(getColor(R.color.dark_gray))
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 4, 0, 0)
            }
            itemLayout.addView(priceTextView)

            quickServicesLayout.addView(itemLayout)
        }

        // Если нет доступных услуг, показываем сообщение
        if (availableServices.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "Нет доступных услуг"
                textSize = 14f
                setTextColor(getColor(R.color.dark_gray))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 16, 0, 16)
            }
            quickServicesLayout.addView(emptyText)
        }

        // Отображаем подключенные услуги
        updateConnectedServicesList()
    }

    /**
     * Подключение услуги
     */
    private fun connectService(service: AvailableService) {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val argJson = "{\"usluga_id\":\"${service.id}\",\"suid\":\"$sessionId\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.setUserUsluga(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.set_user_usluga",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    val message = result?.message ?: "Услуга ${service.name} подключена"
                    showServiceResultDialog(message, true)

                    // Обновляем статус в модели
                    service.isConnected = true

                    // Добавляем услугу в список подключенных
                    addToUserServices(service)
                    // Обновляем UI для этой услуги
                    updateServiceButton(service.id, isConnected = true)
                    // Обновляем список подключенных услуг внизу
                    updateConnectedServicesList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { parseErrorFromResponse(it) } ?: "Ошибка подключения"
                    showServiceResultDialog(errorMessage, false)
                }
            } catch (e: Exception) {
                showServiceResultDialog("Ошибка сети: ${e.message}", false)
            }
        }
    }

    /**
     * Отключение услуги
     */
    private fun disconnectService(service: AvailableService) {
        val sessionId = sessionManager.getSessionId()
        if (sessionId.isNullOrEmpty()) {
            Toast.makeText(this, "Ошибка: сессия не найдена", Toast.LENGTH_SHORT).show()
            return
        }

        val argJson = "{\"usluga_id\":\"${service.id}\",\"suid\":\"$sessionId\"}"

        lifecycleScope.launch {
            try {
                val response = apiService.removeUserUsluga(
                    format = "json",
                    context = "web",
                    model = "users",
                    method = "web_cabinet.remove_user_usluga",
                    args = argJson
                )

                if (response.isSuccessful && response.body() != null) {
                    val result = response.body()
                    val message = result?.message ?: "Услуга ${service.name} отключена"
                    showServiceResultDialog(message, true)

                    // Обновляем статус в модели
                    service.isConnected = false

                    // Удаляем услугу из списка подключенных
                    removeFromUserServices(service.id)
                    // Обновляем UI для этой услуги
                    updateServiceButton(service.id, isConnected = false)
                    // Обновляем список подключенных услуг внизу
                    updateConnectedServicesList()
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = errorBody?.let { parseErrorFromResponse(it) } ?: "Ошибка отключения"
                    showServiceResultDialog(errorMessage, false)
                }
            } catch (e: Exception) {
                showServiceResultDialog("Ошибка сети: ${e.message}", false)
            }
        }
    }

    /**
     * Добавляет услугу в список подключенных (для UI)
     */
    private fun addToUserServices(service: AvailableService) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val userService = UserService(
            id = service.id,
            serviceName = service.fullName,
            enableDate = currentDate,
            activated = "1"
        )
        userServices.add(userService)
        android.util.Log.d("SERVICES_DEBUG", "Added to userServices: id=${service.id}, name=${service.name}")
    }

    /**
     * Удаляет услугу из списка подключенных
     */
    private fun removeFromUserServices(serviceId: String) {
        userServices.removeAll { it.id == serviceId }
        android.util.Log.d("SERVICES_DEBUG", "Removed from userServices: id=$serviceId")
    }

    /**
     * Обновляет кнопку для конкретной услуги
     */
    private fun updateServiceButton(serviceId: String, isConnected: Boolean) {
        val quickServicesLayout = binding.quickServicesLayout
        for (i in 0 until quickServicesLayout.childCount) {
            val itemLayout = quickServicesLayout.getChildAt(i) as LinearLayout
            if (itemLayout.tag == serviceId && itemLayout.childCount > 1) {
                val button = itemLayout.getChildAt(1) as MaterialButton
                button.text = if (isConnected) "Отключить" else "Подключить"
                button.backgroundTintList = getColorStateList(
                    if (isConnected) R.color.red else R.color.purple_500
                )
                android.util.Log.d("SERVICES_DEBUG", "Updated button for service $serviceId, isConnected=$isConnected")
                break
            }
        }
    }

    /**
     * Обновляет список подключенных услуг внизу
     */
    private fun updateConnectedServicesList() {
        val activeServicesLayout = binding.activeServicesLayout
        activeServicesLayout.removeAllViews()

        userServices.forEach { service ->
            val itemLayout = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                gravity = android.view.Gravity.CENTER_VERTICAL
                setPadding(0, 0, 0, 16)
            }

            val infoLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    1f
                )
            }

            val nameTextView = TextView(this).apply {
                text = service.serviceName
                textSize = 14f
                setTypeface(null, Typeface.BOLD)
                setTextColor(getColor(R.color.black))
            }

            val dateTextView = TextView(this).apply {
                text = "Подключено: ${service.enableDate.split(" ").firstOrNull() ?: service.enableDate}"
                textSize = 12f
                setTextColor(getColor(R.color.dark_gray))
            }

            infoLayout.addView(nameTextView)
            infoLayout.addView(dateTextView)

            val statusTextView = TextView(this).apply {
                text = if (service.activated == "1") "Активна" else "Неактивна"
                textSize = 12f
                setTextColor(getColor(if (service.activated == "1") R.color.green else R.color.dark_gray))
                setPadding(8, 4, 8, 4)
                background = getDrawable(R.drawable.status_background_gray)
            }

            itemLayout.addView(infoLayout)
            itemLayout.addView(statusTextView)
            activeServicesLayout.addView(itemLayout)
        }

        if (userServices.isEmpty()) {
            val emptyText = TextView(this).apply {
                text = "Нет подключенных услуг"
                textSize = 14f
                setTextColor(getColor(R.color.dark_gray))
                gravity = android.view.Gravity.CENTER
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                setPadding(0, 16, 0, 16)
            }
            activeServicesLayout.addView(emptyText)
        }
    }

    /**
     * Показывает диалог с результатом
     */
    private fun showServiceResultDialog(message: String, isSuccess: Boolean) {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle(if (isSuccess) "Успешно" else "Ошибка")
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()

        try {
            val titleView = dialog.findViewById<android.widget.TextView>(androidx.appcompat.R.id.alertTitle)
            titleView?.setTextColor(getColor(if (isSuccess) R.color.green else R.color.red))
        } catch (e: Exception) {
            // Игнорируем
        }
    }

    /**
     * Парсит ошибку из ответа API
     */
    private fun parseErrorFromResponse(errorBody: String): String {
        return try {
            val json = JSONObject(errorBody)
            json.optString("error", json.optString("message", "Неизвестная ошибка"))
        } catch (e: Exception) {
            "Ошибка выполнения операции"
        }
    }

    private fun setupClickListeners() {
        binding.navHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navServices.setOnClickListener {
            Toast.makeText(this, "Услуги", Toast.LENGTH_SHORT).show()
        }

        binding.navPayment.setOnClickListener {
            val intent = Intent(this, PaysystemsActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
        }

        binding.navSupport.setOnClickListener {
            val intent = Intent(this, SupportActivity::class.java)
            startActivity(intent)
            overridePendingTransition(0, 0)
            finish()
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