package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.AppointmentCreateRequest
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.LocalDate

data class AgendaUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val appointments: List<BackendAppointmentDto> = emptyList()
)

class AgendaViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AgendaUiState())
    val uiState: StateFlow<AgendaUiState> = _uiState

    fun loadAppointments(
        context: Context,
        date: LocalDate
    ) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = AgendaUiState(
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
                val response = RetrofitClient.appointmentApi.getAppointments(
                    token = token,
                    date = date.toString()
                )

                if (response.isSuccessful) {
                    _uiState.value = AgendaUiState(
                        isLoading = false,
                        appointments = response.body().orEmpty()
                    )
                } else {
                    _uiState.value = AgendaUiState(
                        isLoading = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per veure l'agenda"
                            else -> "No s'ha pogut carregar l'agenda"
                        }
                    )
                }
            } catch (e: ConnectException) {
                _uiState.value = AgendaUiState(
                    isLoading = false,
                    errorMessage = "No es pot connectar amb el backend"
                )
            } catch (e: SocketTimeoutException) {
                _uiState.value = AgendaUiState(
                    isLoading = false,
                    errorMessage = "El backend triga massa a respondre"
                )
            } catch (e: Exception) {
                _uiState.value = AgendaUiState(
                    isLoading = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }

    fun createAppointment(
        context: Context,
        selectedDate: LocalDate,
        patientId: Long,
        dentistId: Long,
        boxId: Long,
        start: String,
        end: String,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
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
                val request = AppointmentCreateRequest(
                    patientId = patientId,
                    dentistId = dentistId,
                    boxId = boxId,
                    startDateTime = "${selectedDate}T${normalizeTime(start)}:00",
                    endDateTime = "${selectedDate}T${normalizeTime(end)}:00",
                    notes = notes?.takeIf { it.isNotBlank() },
                    status = "SCHEDULED",
                    active = true
                )

                val response = RetrofitClient.appointmentApi.createAppointment(
                    token = token,
                    request = request
                )

                if (response.isSuccessful) {
                    loadAppointments(context, selectedDate)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = when (response.code()) {
                            400 -> "Les dades de la cita no són correctes"
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per crear cites"
                            else -> "No s'ha pogut crear la cita"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }

    fun deleteAppointment(
        context: Context,
        selectedDate: LocalDate,
        appointmentId: Long,
        onSuccess: () -> Unit
    ) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No s'ha trobat cap sessió activa"
            )
            return
        }

        viewModelScope.launch {
            try {
                val response = RetrofitClient.appointmentApi.deleteAppointment(
                    token = token,
                    appointmentId = appointmentId
                )

                if (response.isSuccessful) {
                    loadAppointments(context, selectedDate)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "No s'ha pogut eliminar la cita"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }

    private fun normalizeTime(value: String): String {
        val clean = value.trim()
        return if (clean.length == 5) clean else clean.padStart(5, '0')
    }
}