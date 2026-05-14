package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.LoginRequest
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class LoginViewModel : ViewModel() {

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun login(
        context: Context,
        identifier: String,
        password: String,
        onSuccess: () -> Unit
    ) {
        val cleanIdentifier = identifier.trim()
        val cleanPassword = password.trim()

        if (cleanIdentifier.isBlank()) {
            _errorMessage.value = "Introdueix l'usuari o email"
            return
        }

        if (cleanPassword.isBlank()) {
            _errorMessage.value = "Introdueix la contrasenya"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val response = RetrofitClient.authApi.login(
                    LoginRequest(
                        identifier = cleanIdentifier,
                        password = cleanPassword
                    )
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    if (body?.token.isNullOrBlank()) {
                        _errorMessage.value = "Resposta incorrecta del servidor"
                    } else {
                        SessionManager(context).saveLogin(body!!)
                        onSuccess()
                    }
                } else {
                    _errorMessage.value = when (response.code()) {
                        400 -> "Petició incorrecta"
                        401 -> "Usuari o contrasenya incorrectes"
                        403 -> "No tens permisos per accedir"
                        404 -> "Endpoint de login no trobat"
                        500 -> "Error intern del servidor"
                        502, 503, 504 -> "El servidor no està disponible ara mateix. Torna-ho a provar en uns segons."
                        else -> "Error de login: ${response.code()}"
                    }
                }
            } catch (e: SocketTimeoutException) {
                _errorMessage.value = "El backend triga massa a respondre. Si està desplegat a Render, pot estar despertant-se. Torna-ho a provar."
            } catch (e: UnknownHostException) {
                _errorMessage.value = "No es pot resoldre l'adreça del backend. Revisa la connexió a Internet."
            } catch (e: ConnectException) {
                _errorMessage.value = "No es pot connectar amb el backend. Revisa la URL, la connexió i el servidor."
            } catch (e: Exception) {
                _errorMessage.value = "Error inesperat: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}