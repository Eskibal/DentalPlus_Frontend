package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.AppointmentCreateRequest
import com.example.dentalplus_frontend.model.AvailableBoxDto
import com.example.dentalplus_frontend.model.AvailableDentistDto
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import com.example.dentalplus_frontend.model.BackendPatientDto
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
    val isSaving: Boolean = false,
    val isLoadingAvailability: Boolean = false,
    val errorMessage: String? = null,
    val appointments: List<BackendAppointmentDto> = emptyList(),
    val patients: List<BackendPatientDto> = emptyList(),
    val availableDentists: List<AvailableDentistDto> = emptyList(),
    val availableBoxes: List<AvailableBoxDto> = emptyList()
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
                val appointmentsResponse = RetrofitClient.appointmentApi.getAppointments(
                    token = token,
                    date = date.toString()
                )

                val patientsResponse = RetrofitClient.patientApi.getPatients(
                    token = token
                )

                if (appointmentsResponse.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        appointments = appointmentsResponse.body().orEmpty(),
                        patients = if (patientsResponse.isSuccessful) {
                            patientsResponse.body().orEmpty()
                        } else {
                            _uiState.value.patients
                        },
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = when (appointmentsResponse.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per veure l'agenda"
                            else -> "No s'ha pogut carregar l'agenda"
                        }
                    )
                }
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

    fun loadAvailability(
        context: Context,
        selectedDate: LocalDate,
        startTime: String
    ) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No s'ha trobat cap sessió activa"
            )
            return
        }

        if (!isValidTime(startTime)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Introdueix una hora d'inici vàlida"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoadingAvailability = true,
                errorMessage = null,
                availableDentists = emptyList(),
                availableBoxes = emptyList()
            )

            try {
                val response = RetrofitClient.appointmentApi.getAvailability(
                    token = token,
                    date = selectedDate.toString(),
                    time = normalizeTime(startTime)
                )

                if (response.isSuccessful) {
                    val availability = response.body()

                    _uiState.value = _uiState.value.copy(
                        isLoadingAvailability = false,
                        availableDentists = availability?.dentists.orEmpty(),
                        availableBoxes = availability?.boxes.orEmpty(),
                        errorMessage = null
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoadingAvailability = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per consultar disponibilitat"
                            else -> "No s'ha pogut consultar la disponibilitat"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingAvailability = false,
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
        treatment: String,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        saveAppointment(
            context = context,
            selectedDate = selectedDate,
            appointmentId = null,
            patientId = patientId,
            dentistId = dentistId,
            boxId = boxId,
            start = start,
            end = end,
            treatment = treatment,
            notes = notes,
            onSuccess = onSuccess
        )
    }

    fun updateAppointment(
        context: Context,
        selectedDate: LocalDate,
        appointmentId: Long,
        patientId: Long,
        dentistId: Long,
        boxId: Long,
        start: String,
        end: String,
        treatment: String,
        notes: String?,
        onSuccess: () -> Unit
    ) {
        saveAppointment(
            context = context,
            selectedDate = selectedDate,
            appointmentId = appointmentId,
            patientId = patientId,
            dentistId = dentistId,
            boxId = boxId,
            start = start,
            end = end,
            treatment = treatment,
            notes = notes,
            onSuccess = onSuccess
        )
    }

    private fun saveAppointment(
        context: Context,
        selectedDate: LocalDate,
        appointmentId: Long?,
        patientId: Long,
        dentistId: Long,
        boxId: Long,
        start: String,
        end: String,
        treatment: String,
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

        if (!isValidTime(start) || !isValidTime(end)) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "L'hora ha de tenir format HH:mm"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                val request = AppointmentCreateRequest(
                    patientId = patientId,
                    dentistId = dentistId,
                    boxId = boxId,
                    startDateTime = "${selectedDate}T${normalizeTime(start)}:00",
                    endDateTime = "${selectedDate}T${normalizeTime(end)}:00",
                    treatment = treatment,
                    notes = notes?.takeIf { it.isNotBlank() },
                    status = "SCHEDULED",
                    active = true
                )

                val response = if (appointmentId == null) {
                    RetrofitClient.appointmentApi.createAppointment(
                        token = token,
                        request = request
                    )
                } else {
                    RetrofitClient.appointmentApi.updateAppointment(
                        token = token,
                        appointmentId = appointmentId,
                        request = request
                    )
                }

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    loadAppointments(context, selectedDate)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = when (response.code()) {
                            400 -> "Les dades de la cita no són correctes"
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per guardar cites"
                            404 -> "No s'ha trobat la cita"
                            else -> "No s'ha pogut guardar la cita"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
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
            _uiState.value = _uiState.value.copy(
                isSaving = true,
                errorMessage = null
            )

            try {
                val response = RetrofitClient.appointmentApi.deleteAppointment(
                    token = token,
                    appointmentId = appointmentId
                )

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isSaving = false)
                    loadAppointments(context, selectedDate)
                    onSuccess()
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per eliminar cites"
                            404 -> "No s'ha trobat la cita"
                            else -> "No s'ha pogut eliminar la cita"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }

    private fun normalizeTime(value: String): String {
        val clean = value.trim()
        return if (clean.length == 5) clean else clean.padStart(5, '0')
    }

    private fun isValidTime(value: String): Boolean {
        return Regex("^\\d{2}:\\d{2}$").matches(value.trim())
    }
}