package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.BackendPatientDto
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.Period

data class PatientDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val patient: BackendPatientDto? = null
) {
    val fullName: String
        get() {
            val person = patient?.person
            return listOfNotNull(
                person?.name,
                person?.firstSurname,
                person?.secondSurname
            ).filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Pacient sense nom" }
        }

    val profileImage: String?
        get() = patient?.person?.profileImage

    val ageText: String
        get() {
            val birthDate = patient?.person?.birthDate ?: return "No disponible"

            return try {
                val age = Period.between(LocalDate.parse(birthDate), LocalDate.now()).years
                "$age anys"
            } catch (e: Exception) {
                "No disponible"
            }
        }

    val genderText: String
        get() {
            return when (patient?.person?.gender?.uppercase()) {
                "MALE", "MAN", "HOME" -> "Home"
                "FEMALE", "WOMAN", "DONA" -> "Dona"
                "OTHER", "ALTRE" -> "Altre"
                else -> "No disponible"
            }
        }

    val birthDateText: String
        get() = patient?.person?.birthDate ?: "No disponible"

    val patientIdText: String
        get() = patient?.patientId?.let { "#$it" } ?: "No disponible"

    val emailText: String
        get() = patient?.person?.email ?: "No disponible"

    val phoneText: String
        get() {
            val prefix = patient?.person?.phonePrefix.orEmpty()
            val number = patient?.person?.phoneNumber.orEmpty()
            return "$prefix $number".trim().ifBlank { "No disponible" }
        }

    val addressText: String
        get() = patient?.person?.address ?: "No disponible"

    val cityText: String
        get() = patient?.person?.city ?: "No disponible"

    val notesText: String
        get() = patient?.notes ?: "No disponible"

    val personNotesText: String
        get() = patient?.person?.notes ?: "No disponible"

    val clinicText: String
        get() = patient?.clinicName ?: "No disponible"

    val medicalAlertText: String
        get() = patient?.medicalAlert ?: "No disponible"
}

class PatientDetailViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PatientDetailUiState())
    val uiState: StateFlow<PatientDetailUiState> = _uiState

    fun loadPatient(context: Context, patientId: Long) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = PatientDetailUiState(
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
                val response = RetrofitClient.patientApi.getPatientById(
                    token = token,
                    patientId = patientId
                )

                if (response.isSuccessful) {
                    _uiState.value = PatientDetailUiState(
                        isLoading = false,
                        patient = response.body()
                    )
                } else {
                    _uiState.value = PatientDetailUiState(
                        isLoading = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per veure aquest pacient"
                            404 -> "No s'ha trobat aquest pacient"
                            else -> "No s'ha pogut carregar el pacient"
                        }
                    )
                }
            } catch (e: ConnectException) {
                _uiState.value = PatientDetailUiState(
                    isLoading = false,
                    errorMessage = "No es pot connectar amb el backend"
                )
            } catch (e: SocketTimeoutException) {
                _uiState.value = PatientDetailUiState(
                    isLoading = false,
                    errorMessage = "El backend triga massa a respondre"
                )
            } catch (e: Exception) {
                _uiState.value = PatientDetailUiState(
                    isLoading = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }
}