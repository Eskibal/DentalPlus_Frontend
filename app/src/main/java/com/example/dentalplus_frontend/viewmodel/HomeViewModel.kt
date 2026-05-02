package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.model.ProfileDto
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.LocalDate

data class HomeUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val profile: ProfileDto? = null,
    val appointments: List<BackendAppointmentDto> = emptyList()
) {
    val personName: String
        get() = profile?.person?.name?.takeIf { it.isNotBlank() }
            ?: profile?.username?.takeIf { it.isNotBlank() }
            ?: "usuari"

    val welcomeText: String
        get() {
            val gender = profile?.person?.gender?.uppercase()

            val greeting = when (gender) {
                "FEMALE", "DONA", "WOMAN" -> "Benvinguda de nou,"
                "MALE", "HOME", "MAN" -> "Benvingut de nou,"
                else -> "Benvingut/da de nou,"
            }

            return "$greeting $personName"
        }

    val todayPatientsCount: Int
        get() = appointments
            .filter { it.active != false }
            .mapNotNull { it.patientId }
            .distinct()
            .size

    val pendingAppointmentsCount: Int
        get() = appointments.count {
            it.active != false && it.status.equals("SCHEDULED", ignoreCase = true)
        }

    val completedAppointmentsCount: Int
        get() = appointments.count {
            it.active != false && it.status.equals("COMPLETED", ignoreCase = true)
        }
}

class HomeViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    fun loadHome(context: Context) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = HomeUiState(
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
                val today = LocalDate.now().toString()

                val profileResponse = RetrofitClient.userApi.getMyProfile(token)
                val appointmentsResponse = RetrofitClient.appointmentApi.getAppointments(
                    token = token,
                    date = today
                )

                val profile = if (profileResponse.isSuccessful) {
                    profileResponse.body()
                } else {
                    null
                }

                val appointments = if (appointmentsResponse.isSuccessful) {
                    appointmentsResponse.body().orEmpty()
                } else {
                    emptyList()
                }

                val errorMessage = when {
                    !profileResponse.isSuccessful -> "No s'ha pogut carregar el perfil"
                    !appointmentsResponse.isSuccessful -> "No s'ha pogut carregar l'agenda d'avui"
                    else -> null
                }

                _uiState.value = HomeUiState(
                    isLoading = false,
                    errorMessage = errorMessage,
                    profile = profile,
                    appointments = appointments
                )
            } catch (e: ConnectException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No es pot connectar amb el backend"
                )
            } catch (e: SocketTimeoutException) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "El backend triga massa a respondre"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }
}