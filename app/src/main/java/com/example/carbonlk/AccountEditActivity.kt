package com.example.carbonlk

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.carbonlk.databinding.ActivityAccountEditBinding
import com.example.carbonlk.data.MockRepository

class AccountEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }

        binding = ActivityAccountEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()
        loadUserData()
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
                systemInsets.bottom
            )

            insets
        }
    }

    private fun loadUserData() {
        binding.firstNameInput.setText(intent.getStringExtra("firstName") ?: MockRepository.currentUser.firstName)
        binding.lastNameInput.setText(intent.getStringExtra("lastName") ?: MockRepository.currentUser.lastName)
        binding.phoneInput.setText(intent.getStringExtra("phone") ?: MockRepository.currentUser.phone)
        binding.emailInput.setText(intent.getStringExtra("email") ?: MockRepository.currentUser.email)
        binding.addressInput.setText(intent.getStringExtra("address") ?: MockRepository.currentUser.address)
    }

    private fun setupClickListeners() {
        binding.saveButton.setOnClickListener {
            val newFirstName = binding.firstNameInput.text.toString()
            val newLastName = binding.lastNameInput.text.toString()
            val newPhone = binding.phoneInput.text.toString()
            val newEmail = binding.emailInput.text.toString()
            val newAddress = binding.addressInput.text.toString()

            if (newFirstName.isEmpty() || newLastName.isEmpty() || newPhone.isEmpty() || newEmail.isEmpty() || newAddress.isEmpty()) {
                Toast.makeText(this, "Все поля должны быть заполнены", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            MockRepository.currentUser = MockRepository.currentUser.copy(
                firstName = newFirstName,
                lastName = newLastName,
                phone = newPhone,
                email = newEmail,
                address = newAddress
            )

            Toast.makeText(this, "Данные сохранены", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}