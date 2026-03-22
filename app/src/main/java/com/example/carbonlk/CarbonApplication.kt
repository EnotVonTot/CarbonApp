package com.example.carbonlk

import android.app.Application
import com.example.carbonlk.network.RetrofitClient
import com.example.carbonlk.utils.SettingsManager

class CarbonApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val settingsManager = SettingsManager(this)
        RetrofitClient.init(settingsManager)
    }
}