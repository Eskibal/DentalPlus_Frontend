package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.DentalSurfaceMarkRequest
import com.example.dentalplus_frontend.model.OdontogramType
import com.example.dentalplus_frontend.model.OdontogramViewModeRequest
import com.example.dentalplus_frontend.model.ToothPart
import com.example.dentalplus_frontend.model.ToothState
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class OdontogramUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val patientId: Long? = null,
    val odontogramId: Long? = null,
    val type: OdontogramType = OdontogramType.ADULT
)

class OdontogramViewModel : ViewModel() {

    private val toothStates = mutableStateMapOf<Int, ToothState>()

    private val _uiState = MutableStateFlow(OdontogramUiState())
    val uiState: StateFlow<OdontogramUiState> = _uiState

    fun getToothState(toothNumber: Int): ToothState {
        return toothStates.getOrPut(toothNumber) {
            ToothState()
        }
    }

    fun updateToothState(
        context: Context,
        patientId: Long,
        toothNumber: Int,
        colors: Map<ToothPart, Color>
    ) {
        val newState = ToothState(
            colors = mutableStateMapOf<ToothPart, Color>().apply {
                putAll(colors)
            }
        )

        toothStates[toothNumber] = newState

        saveToothStateToBackend(
            context = context,
            patientId = patientId,
            toothNumber = toothNumber,
            colors = colors
        )
    }

    fun loadOrCreateOdontogram(
        context: Context,
        patientId: Long,
        selectedType: OdontogramType
    ) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "No s'ha trobat cap sessió activa"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                patientId = patientId,
                type = selectedType
            )

            try {
                val getResponse = RetrofitClient.odontogramApi.getOdontogramByPatient(
                    token = token,
                    patientId = patientId
                )

                val odontogram = if (getResponse.isSuccessful) {
                    getResponse.body()
                } else {
                    val createResponse = RetrofitClient.odontogramApi.createOdontogram(
                        token = token,
                        patientId = patientId
                    )

                    if (createResponse.isSuccessful) {
                        createResponse.body()
                    } else {
                        null
                    }
                }

                RetrofitClient.odontogramApi.updateViewModeByPatient(
                    token = token,
                    patientId = patientId,
                    request = OdontogramViewModeRequest(
                        viewMode = selectedType.toBackendViewMode()
                    )
                )

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    odontogramId = odontogram?.id,
                    type = selectedType
                )

                loadExistingMarks(
                    context = context,
                    patientId = patientId,
                    type = selectedType
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "No s'ha pogut carregar l'odontograma"
                )
            }
        }
    }

    private fun loadExistingMarks(
        context: Context,
        patientId: Long,
        type: OdontogramType
    ) {
        val token = SessionManager(context).getBearerToken() ?: return

        viewModelScope.launch {
            try {
                val teeth = getAllTeethForType(type)

                teeth.forEach { toothNumber ->
                    val stateColors = mutableStateMapOf<ToothPart, Color>()

                    ToothPart.entries.forEach { part ->
                        val surfaceType = part.toBackendSurfaceType() ?: return@forEach

                        val response = RetrofitClient.odontogramApi.getSurfaceMarks(
                            token = token,
                            patientId = patientId,
                            pieceNumber = toothNumber,
                            surfaceType = surfaceType
                        )

                        if (response.isSuccessful) {
                            val activeMark = response.body()
                                .orEmpty()
                                .lastOrNull { it.active != false }

                            val color = activeMark?.markType?.toColor()

                            if (color != null) {
                                stateColors[part] = color
                            }
                        }
                    }

                    toothStates[toothNumber] = ToothState(stateColors)
                }
            } catch (_: Exception) {
            }
        }
    }

    private fun saveToothStateToBackend(
        context: Context,
        patientId: Long,
        toothNumber: Int,
        colors: Map<ToothPart, Color>
    ) {
        val token = SessionManager(context).getBearerToken() ?: return

        viewModelScope.launch {
            try {
                ToothPart.entries.forEach { part ->
                    val surfaceType = part.toBackendSurfaceType() ?: return@forEach

                    val existingMarksResponse = RetrofitClient.odontogramApi.getSurfaceMarks(
                        token = token,
                        patientId = patientId,
                        pieceNumber = toothNumber,
                        surfaceType = surfaceType
                    )

                    if (existingMarksResponse.isSuccessful) {
                        existingMarksResponse.body()
                            .orEmpty()
                            .filter { it.active != false && it.id != null }
                            .forEach { mark ->
                                RetrofitClient.odontogramApi.deleteSurfaceMark(
                                    token = token,
                                    markId = mark.id!!
                                )
                            }
                    }

                    val selectedColor = colors[part]

                    if (selectedColor != null && selectedColor != Color.White) {
                        val request = selectedColor.toBackendMarkRequest()

                        RetrofitClient.odontogramApi.createSurfaceMark(
                            token = token,
                            patientId = patientId,
                            pieceNumber = toothNumber,
                            surfaceType = surfaceType,
                            request = request
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "No s'han pogut guardar els canvis de la dent"
                )
            }
        }
    }

    private fun getAllTeethForType(type: OdontogramType): List<Int> {
        val child = listOf(
            55, 54, 53, 52, 51,
            61, 62, 63, 64, 65,
            85, 84, 83, 82, 81,
            71, 72, 73, 74, 75
        )

        val adult = listOf(
            18, 17, 16, 15, 14, 13, 12, 11,
            21, 22, 23, 24, 25, 26, 27, 28,
            48, 47, 46, 45, 44, 43, 42, 41,
            31, 32, 33, 34, 35, 36, 37, 38
        )

        return when (type) {
            OdontogramType.CHILD -> child
            OdontogramType.ADULT -> adult
            OdontogramType.BOTH -> adult + child
        }
    }
}

fun OdontogramType.toBackendViewMode(): String {
    return when (this) {
        OdontogramType.CHILD -> "TEMPORARY"
        OdontogramType.ADULT -> "PERMANENT"
        OdontogramType.BOTH -> "MIXED"
    }
}

fun ToothPart.toBackendSurfaceType(): String? {
    return when (this) {
        ToothPart.MESIAL -> "MESIAL"
        ToothPart.DISTAL -> "DISTAL"
        ToothPart.LINGUAL -> "LINGUAL"
        ToothPart.OCCLUSAL -> "OCCLUSAL"
        ToothPart.CENTER -> "OCCLUSAL"
    }
}

fun Color.toBackendMarkRequest(): DentalSurfaceMarkRequest {
    return when (this) {
        Color(0xFFDC0000) -> DentalSurfaceMarkRequest(
            markType = "CARIES",
            markState = "PENDING",
            notes = "Patologia o lesió"
        )

        Color(0xFF0000DC) -> DentalSurfaceMarkRequest(
            markType = "FILLING",
            markState = "DONE",
            notes = "Tractament ja fet"
        )

        Color(0xFF00DC00) -> DentalSurfaceMarkRequest(
            markType = "RADIOGRAPH_CARIES",
            markState = "PENDING",
            notes = "Càries radiogràfica"
        )

        Color(0xFFFFD600) -> DentalSurfaceMarkRequest(
            markType = "FISSURE_SEALANT",
            markState = "DONE",
            notes = "Segellat de foses i fissures"
        )

        Color.Black -> DentalSurfaceMarkRequest(
            markType = "NATURAL_ABSENCE",
            markState = "NATURAL",
            notes = "Absència natural"
        )

        else -> DentalSurfaceMarkRequest(
            markType = "CARIES",
            markState = "PENDING",
            notes = "Marca dental"
        )
    }
}

fun String.toColor(): Color? {
    return when (uppercase()) {
        "CARIES" -> Color(0xFFDC0000)
        "FILLING" -> Color(0xFF0000DC)
        "RADIOGRAPH_CARIES" -> Color(0xFF00DC00)
        "FISSURE_SEALANT" -> Color(0xFFFFD600)
        "NATURAL_ABSENCE" -> Color.Black
        else -> null
    }
}