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
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val patients: List<BackendPatientDto> = emptyList(),
    val todayAppointments: List<BackendAppointmentDto> = emptyList()
) {
    val activePatients: List<BackendPatientDto>
        get() = patients.filter { it.active != false }

    val todayPatientIds: Set<Long>
        get() = todayAppointments
            .filter { it.active != false }
            .mapNotNull { it.patientId }
            .toSet()

    val todayPatients: List<BackendPatientDto>
        get() = activePatients.filter { patient ->
            patient.patientId != null && todayPatientIds.contains(patient.patientId)
        }

    val otherPatients: List<BackendPatientDto>
        get() = activePatients.filter { patient ->
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
                errorMessage = null,
                successMessage = null
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
                    _uiState.value = _uiState.value.copy(
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
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = when (patientsResponse.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per veure els pacients"
                            else -> "No s'ha pogut carregar la llista de pacients"
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

    fun createPatient(
        context: Context,
        patient: BackendPatientDto,
        currentSearch: String,
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
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = RetrofitClient.patientApi.createPatient(
                    token = token,
                    patient = patient
                )

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Pacient creat correctament"
                    )
                    onSuccess()
                    loadPatients(context, currentSearch)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per crear pacients"
                            else -> "No s'ha pogut crear el pacient"
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

    fun updatePatient(
        context: Context,
        patientId: Long,
        patient: BackendPatientDto,
        currentSearch: String,
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
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = RetrofitClient.patientApi.updatePatient(
                    token = token,
                    patientId = patientId,
                    patient = patient
                )

                if (response.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        successMessage = "Pacient actualitzat correctament"
                    )
                    onSuccess()
                    loadPatients(context, currentSearch)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per modificar pacients"
                            404 -> "No s'ha trobat aquest pacient"
                            else -> "No s'ha pogut actualitzar el pacient"
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

    fun deactivatePatient(
        context: Context,
        patient: BackendPatientDto,
        currentSearch: String,
        onSuccess: () -> Unit
    ) {
        val patientId = patient.patientId

        if (patientId == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Aquest pacient no té ID"
            )
            return
        }

        val inactivePatient = patient.copy(active = false)

        updatePatient(
            context = context,
            patientId = patientId,
            patient = inactivePatient,
            currentSearch = currentSearch,
            onSuccess = onSuccess
        )
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }
}