package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.ProfileDto
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.Period

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val profile: ProfileDto? = null
) {
    val fullName: String
        get() {
            val person = profile?.person
            return listOfNotNull(
                person?.name,
                person?.firstSurname,
                person?.secondSurname
            ).filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { profile?.username ?: "Usuari" }
        }

    val roleText: String
        get() {
            val role = profile?.roles?.firstOrNull()?.roleType?.uppercase()
            return when (role) {
                "ADMIN" -> "Administrador"
                "DENTIST" -> "Dentista"
                "RECEPTIONIST" -> "Recepcionista"
                "PATIENT" -> "Pacient"
                else -> "Usuari"
            }
        }

    val clinicName: String
        get() = profile?.roles?.firstOrNull()?.clinicName ?: "Clínica no disponible"

    val genderText: String
        get() {
            return when (profile?.person?.gender?.uppercase()) {
                "MALE", "MAN", "HOME" -> "Home"
                "FEMALE", "WOMAN", "DONA" -> "Dona"
                "OTHER", "ALTRE" -> "Altre"
                else -> "No disponible"
            }
        }

    val ageText: String
        get() {
            val birthDate = profile?.person?.birthDate ?: return "No disponible"

            return try {
                val age = Period.between(LocalDate.parse(birthDate), LocalDate.now()).years
                "$age anys"
            } catch (e: Exception) {
                "No disponible"
            }
        }

    val emailText: String
        get() = profile?.person?.email ?: "No disponible"

    val phoneText: String
        get() {
            val prefix = profile?.person?.phonePrefix.orEmpty()
            val number = profile?.person?.phoneNumber.orEmpty()
            return "$prefix $number".trim().ifBlank { "No disponible" }
        }

    val cityText: String
        get() = profile?.person?.city ?: "No disponible"

    val addressText: String
        get() = profile?.person?.address ?: "No disponible"
}

class ProfileViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState

    fun loadProfile(context: Context) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = ProfileUiState(
                isLoading = false,
                errorMessage = "No s'ha trobat cap sessió activa"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )

            try {
                val response = RetrofitClient.userApi.getMyProfile(token)

                if (response.isSuccessful) {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        profile = response.body()
                    )
                } else {
                    _uiState.value = ProfileUiState(
                        isLoading = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per veure aquest perfil"
                            else -> "No s'ha pogut carregar el perfil"
                        }
                    )
                }
            } catch (e: ConnectException) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = "No es pot connectar amb el backend"
                )
            } catch (e: SocketTimeoutException) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = "El backend triga massa a respondre"
                )
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(
                    isLoading = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }
}