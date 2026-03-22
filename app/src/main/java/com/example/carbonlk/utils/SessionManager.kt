package com.example.carbonlk.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.example.carbonlk.model.FullUserResponse

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("user_session", Context.MODE_PRIVATE)
    private val gson = Gson()

    companion object {
        private const val KEY_SESSION_ID = "session_id"
        private const val KEY_ACCOUNT_ID = "account_id"
        private const val KEY_USER_DATA_JSON = "user_data_json"
        private const val KEY_USER_LOGIN = "user_login"
        private const val KEY_USER_FULL_NAME = "user_full_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun saveSession(
        sessionId: String,
        login: String,
        fullName: String
    ) {
        prefs.edit().apply {
            putString(KEY_SESSION_ID, sessionId)
            putString(KEY_USER_LOGIN, login)
            putString(KEY_USER_FULL_NAME, fullName)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    /**
     * Сохраняет лицевой счёт
     */
    fun saveAccountId(accountId: String) {
        prefs.edit().putString(KEY_ACCOUNT_ID, accountId).apply()
    }

    /**
     * Сохраняет полное имя пользователя
     */
    fun saveUserFullName(fullName: String) {
        prefs.edit().putString(KEY_USER_FULL_NAME, fullName).apply()
    }

    /**
     * Сохраняет email пользователя
     */
    fun saveUserEmail(email: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email).apply()
    }

    /**
     * Сохраняет полный JSON ответа от web_cabinet.get_user
     */
    fun saveUserData(userData: FullUserResponse) {
        val json = gson.toJson(userData)
        prefs.edit().putString(KEY_USER_DATA_JSON, json).apply()
    }

    /**
     * Возвращает сохранённые данные пользователя
     */
    fun getUserData(): FullUserResponse? {
        val json = prefs.getString(KEY_USER_DATA_JSON, null)
        return if (json != null) {
            try {
                gson.fromJson(json, FullUserResponse::class.java)
            } catch (e: Exception) {
                null
            }
        } else null
    }

    fun getSessionId(): String? = prefs.getString(KEY_SESSION_ID, null)
    fun getAccountId(): String? = prefs.getString(KEY_ACCOUNT_ID, null)
    fun getUserLogin(): String? = prefs.getString(KEY_USER_LOGIN, null)
    fun getUserFullName(): String? = prefs.getString(KEY_USER_FULL_NAME, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}