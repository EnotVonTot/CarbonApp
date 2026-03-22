package com.example.carbonlk

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.carbonlk.databinding.ActivitySplashBinding
import com.example.carbonlk.utils.SessionManager

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        // ВРЕМЕННО: очищаем старую сессию для тестирования
        sessionManager.clearSession()



        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            if (sessionManager.isLoggedIn()) {
                // Если уже авторизован, переходим на главный экран
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // Иначе на экран входа
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }, 2000)
    }
}