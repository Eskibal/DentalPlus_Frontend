package com.example.dentalplus_frontend.session

import android.content.Context
import com.example.dentalplus_frontend.model.LoginResponse

class SessionManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(
        "dentalplus_session",
        Context.MODE_PRIVATE
    )

    fun saveLogin(response: LoginResponse) {
        sharedPreferences.edit()
            .putString("auth_token", response.token)
            .putLong("user_id", response.userId)
            .putString("username", response.profile?.username)
            .putString("person_name", response.profile?.person?.name)
            .putString("person_email", response.profile?.person?.email)
            .apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }

    fun getBearerToken(): String? {
        val token = getToken()
        return if (token.isNullOrBlank()) {
            null
        } else {
            "Bearer $token"
        }
    }

    fun getUserId(): Long {
        return sharedPreferences.getLong("user_id", -1L)
    }

    fun getUsername(): String? {
        return sharedPreferences.getString("username", null)
    }

    fun getPersonName(): String? {
        return sharedPreferences.getString("person_name", null)
    }

    fun getPersonEmail(): String? {
        return sharedPreferences.getString("person_email", null)
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }

    fun clearSession() {
        sharedPreferences.edit().clear().apply()
    }
}