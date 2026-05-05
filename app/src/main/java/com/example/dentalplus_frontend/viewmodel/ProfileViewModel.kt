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
import java.time.format.DateTimeParseException

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val fullName: String = "Usuari sense nom",
    val roleText: String = "No disponible",
    val clinicName: String = "No disponible",

    val ageText: String = "No disponible",
    val genderText: String = "No disponible",

    val emailText: String = "No disponible",
    val phoneText: String = "No disponible",
    val cityText: String = "No disponible",
    val addressText: String = "No disponible",

    val profileImage: String? = null
)

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
                    val profile = response.body()

                    if (profile != null) {
                        _uiState.value = profile.toProfileUiState()
                    } else {
                        _uiState.value = ProfileUiState(
                            isLoading = false,
                            errorMessage = "No s'han rebut dades del perfil"
                        )
                    }
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

private fun ProfileDto.toProfileUiState(): ProfileUiState {
    val person = person

    val fullName = listOfNotNull(
        person?.name,
        person?.firstSurname,
        person?.secondSurname
    )
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { username ?: "Usuari sense nom" }

    val mainRole = roles
        ?.firstOrNull { it.active != false }
        ?: roles?.firstOrNull()

    return ProfileUiState(
        isLoading = false,
        errorMessage = null,

        fullName = fullName,
        roleText = mainRole?.roleType.toCatalanRole(),
        clinicName = mainRole?.clinicName.ifNullOrBlank("No disponible"),

        ageText = person?.birthDate.toAgeText(),
        genderText = person?.gender.toCatalanGender(),

        emailText = person?.email.ifNullOrBlank("No disponible"),
        phoneText = buildPhoneText(
            prefix = person?.phonePrefix,
            number = person?.phoneNumber
        ),
        cityText = person?.city.ifNullOrBlank("No disponible"),
        addressText = person?.address.ifNullOrBlank("No disponible"),

        profileImage = person?.profileImage
    )
}

private fun String?.ifNullOrBlank(defaultValue: String): String {
    return if (this.isNullOrBlank()) defaultValue else this
}

private fun String?.toCatalanRole(): String {
    return when (this?.uppercase()) {
        "ADMIN" -> "Administrador"
        "DENTIST" -> "Dentista"
        "RECEPTIONIST" -> "Recepcionista"
        "PATIENT" -> "Pacient"
        else -> "No disponible"
    }
}

private fun String?.toCatalanGender(): String {
    return when (this?.uppercase()) {
        "MALE", "MASCULINO", "HOME" -> "Home"
        "FEMALE", "FEMENINO", "DONA" -> "Dona"
        "OTHER", "ALTRE" -> "Altre"
        else -> "No disponible"
    }
}

private fun String?.toAgeText(): String {
    if (this.isNullOrBlank()) {
        return "No disponible"
    }

    return try {
        val birthDate = LocalDate.parse(this)
        val age = Period.between(birthDate, LocalDate.now()).years
        "$age anys"
    } catch (e: DateTimeParseException) {
        "No disponible"
    }
}

private fun buildPhoneText(
    prefix: String?,
    number: String?
): String {
    val cleanPrefix = prefix.orEmpty().trim()
    val cleanNumber = number.orEmpty().trim()

    return when {
        cleanPrefix.isNotBlank() && cleanNumber.isNotBlank() -> "$cleanPrefix $cleanNumber"
        cleanNumber.isNotBlank() -> cleanNumber
        else -> "No disponible"
    }
}