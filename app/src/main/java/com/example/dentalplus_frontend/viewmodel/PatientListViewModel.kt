package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class PatientListUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val patients: List<BackendPatientDto> = emptyList(),
    val todayAppointments: List<BackendAppointmentDto> = emptyList()
) {
    val todayPatientIds: Set<Long>
        get() = todayAppointments
            .filter { it.active != false }
            .mapNotNull { it.patientId }
            .toSet()

    val todayPatients: List<BackendPatientDto>
        get() = patients.filter { patient ->
            patient.patientId != null && todayPatientIds.contains(patient.patientId)
        }

    val otherPatients: List<BackendPatientDto>
        get() = patients.filter { patient ->
            patient.patientId == null || !todayPatientIds.contains(patient.patientId)
        }
}

class PatientListViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PatientListUiState())
    val uiState: StateFlow<PatientListUiState> = _uiState

    fun loadPatients(context: Context, search: String? = null) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = PatientListUiState(
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

                val patientsResponse = RetrofitClient.patientApi.getPatients(
                    token = token,
                    search = search?.takeIf { it.isNotBlank() }
                )

                val appointmentsResponse = RetrofitClient.appointmentApi.getAppointments(
                    token = token,
                    date = today
                )

                if (patientsResponse.isSuccessful) {
                    _uiState.value = PatientListUiState(
                        isLoading = false,
                        patients = patientsResponse.body().orEmpty(),
                        todayAppointments = if (appointmentsResponse.isSuccessful) {
                            appointmentsResponse.body().orEmpty()
                        } else {
                            emptyList()
                        },
                        errorMessage = if (!appointmentsResponse.isSuccessful) {
                            "S'han carregat els pacients, però no l'agenda d'avui"
                        } else {
                            null
                        }
                    )
                } else {
                    _uiState.value = PatientListUiState(
                        isLoading = false,
                        errorMessage = when (patientsResponse.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per veure els pacients"
                            else -> "No s'ha pogut carregar la llista de pacients"
                        }
                    )
                }
            } catch (e: ConnectException) {
                _uiState.value = PatientListUiState(
                    isLoading = false,
                    errorMessage = "No es pot connectar amb el backend"
                )
            } catch (e: SocketTimeoutException) {
                _uiState.value = PatientListUiState(
                    isLoading = false,
                    errorMessage = "El backend triga massa a respondre"
                )
            } catch (e: Exception) {
                _uiState.value = PatientListUiState(
                    isLoading = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }
}