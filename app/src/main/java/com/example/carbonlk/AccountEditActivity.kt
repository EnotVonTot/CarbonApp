package com.example.carbonlk

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.carbonlk.databinding.ActivityAccountEditBinding
import com.example.carbonlk.data.MockRepository
import java.util.regex.Pattern

class AccountEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadUserData()
        setupClickListeners()
    }

    private fun loadUserData() {
        val user = MockRepository.currentUser

        binding.firstNameInput.setText(user.firstName)
        binding.patronymicInput.setText(user.middleName)
        binding.lastNameInput.setText(user.lastName)
        binding.phoneInput.setText(user.phone)
        binding.emailInput.setText(user.email)
        binding.addressInput.setText(user.address)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            if (validateFields()) {
                saveUserData()
            }
        }
    }

    private fun validateFields(): Boolean {
        val firstName = binding.firstNameInput.text.toString().trim()
        val patronymic = binding.patronymicInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val address = binding.addressInput.text.toString().trim()

        var isValid = true

        // Валидация имени (обязательное поле)
        if (!validateName(firstName)) {
            binding.firstNameLayout.error = "Только буквы, дефис и пробел. 2-50 символов"
            isValid = false
        } else {
            binding.firstNameLayout.error = null
        }

        // Валидация отчества (необязательное поле)
        if (patronymic.isNotEmpty() && !validateName(patronymic)) {
            binding.patronymicLayout.error = "Только буквы, дефис и пробел. 2-50 символов"
            isValid = false
        } else {
            binding.patronymicLayout.error = null
        }

        // Валидация фамилии (обязательное поле)
        if (!validateName(lastName)) {
            binding.lastNameLayout.error = "Только буквы, дефис и пробел. 2-50 символов"
            isValid = false
        } else {
            binding.lastNameLayout.error = null
        }

        // Валидация телефона
        if (!validatePhone(phone)) {
            binding.phoneLayout.error = "Введите корректный номер телефона (только +, цифры, пробелы, дефисы)"
            isValid = false
        } else {
            binding.phoneLayout.error = null
        }

        // Валидация email
        if (!validateEmail(email)) {
            binding.emailLayout.error = "Введите корректный email"
            isValid = false
        } else {
            binding.emailLayout.error = null
        }

        // Валидация адреса
        if (address.isEmpty()) {
            binding.addressLayout.error = "Адрес не может быть пустым"
            isValid = false
        } else if (address.length > 200) {
            binding.addressLayout.error = "Адрес не может быть длиннее 200 символов"
            isValid = false
        } else {
            binding.addressLayout.error = null
        }

        return isValid
    }

    /**
     * Валидация имени/фамилии/отчества
     * Разрешены: буквы (любого алфавита), дефис, пробел
     * Минимум 2 символа, максимум 50
     */
    private fun validateName(name: String): Boolean {
        val pattern = Pattern.compile("^[a-zA-Zа-яА-ЯёЁ\\-\\s]{2,50}$")
        return pattern.matcher(name).matches()
    }

    /**
     * Валидация телефона
     * Разрешены: +, цифры, пробелы, дефисы
     * Минимум 10 символов, максимум 20
     */
    private fun validatePhone(phone: String): Boolean {
        val pattern = Pattern.compile("^[+\\d\\s\\-]{10,20}$")
        return pattern.matcher(phone).matches()
    }

    /**
     * Валидация email
     * Стандартный формат email
     */
    private fun validateEmail(email: String): Boolean {
        val pattern = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
        )
        return pattern.matcher(email).matches()
    }

    private fun saveUserData() {
        val firstName = binding.firstNameInput.text.toString().trim()
        val middleName = binding.patronymicInput.text.toString().trim()
        val lastName = binding.lastNameInput.text.toString().trim()
        val phone = binding.phoneInput.text.toString().trim()
        val email = binding.emailInput.text.toString().trim()
        val address = binding.addressInput.text.toString().trim()

        // Сохраняем в репозиторий (поля теперь раздельные)
        MockRepository.currentUser = MockRepository.currentUser.copy(
            firstName = firstName,
            middleName = middleName,
            lastName = lastName,
            phone = phone,
            email = email,
            address = address
        )

        Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
        finish()
    }

}