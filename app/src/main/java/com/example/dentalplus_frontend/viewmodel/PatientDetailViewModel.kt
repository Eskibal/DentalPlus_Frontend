package com.example.dentalplus_frontend.viewmodel

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dentalplus_frontend.model.BackendPatientDto
import com.example.dentalplus_frontend.model.DocumentDto
import com.example.dentalplus_frontend.network.RetrofitClient
import com.example.dentalplus_frontend.session.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.time.LocalDate
import java.time.Period

data class PatientDetailUiState(
    val isLoading: Boolean = false,
    val isUploadingDocument: Boolean = false,
    val isDeletingDocument: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val patient: BackendPatientDto? = null,
    val documents: List<DocumentDto> = emptyList()
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

    val hasMedicalAlert: Boolean
        get() = !patient?.medicalAlert.isNullOrBlank()
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
                errorMessage = null,
                successMessage = null
            )

            try {
                val patientResponse = withContext(Dispatchers.IO) {
                    RetrofitClient.patientApi.getPatientById(
                        token = token,
                        patientId = patientId
                    )
                }

                if (patientResponse.isSuccessful) {
                    val patient = patientResponse.body()

                    val documents = loadDocumentsInternal(
                        token = token,
                        patientId = patientId,
                        fallbackDocuments = patient?.documents.orEmpty()
                    )

                    _uiState.value = PatientDetailUiState(
                        isLoading = false,
                        patient = patient,
                        documents = documents
                    )
                } else {
                    _uiState.value = PatientDetailUiState(
                        isLoading = false,
                        errorMessage = when (patientResponse.code()) {
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

    fun uploadDocuments(
        context: Context,
        patientId: Long,
        uris: List<Uri>
    ) {
        val token = SessionManager(context).getBearerToken()

        if (token.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No s'ha trobat cap sessió activa"
            )
            return
        }

        if (uris.isEmpty()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Selecciona almenys un PDF"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isUploadingDocument = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val result = withContext(Dispatchers.IO) {
                    uploadDocumentsInternal(
                        context = context,
                        token = token,
                        patientId = patientId,
                        uris = uris
                    )
                }

                val currentPatient = _uiState.value.patient

                val documents = loadDocumentsInternal(
                    token = token,
                    patientId = patientId,
                    fallbackDocuments = currentPatient?.documents.orEmpty()
                )

                _uiState.value = _uiState.value.copy(
                    isUploadingDocument = false,
                    documents = documents,
                    successMessage = if (result.failedMessages.isEmpty()) {
                        if (result.uploadedCount == 1) {
                            "Document pujat correctament"
                        } else {
                            "${result.uploadedCount} documents pujats correctament"
                        }
                    } else {
                        null
                    },
                    errorMessage = if (result.failedMessages.isNotEmpty()) {
                        result.failedMessages.joinToString("\n")
                    } else {
                        null
                    }
                )

                loadPatient(context, patientId)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingDocument = false,
                    errorMessage = "No s'han pogut pujar els documents: ${e.message}"
                )
            }
        }
    }

    fun deleteDocument(
        context: Context,
        patientId: Long,
        documentId: Long
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
                isDeletingDocument = true,
                errorMessage = null,
                successMessage = null
            )

            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.documentApi.deleteDocument(
                        token = token,
                        documentId = documentId
                    )
                }

                if (response.isSuccessful) {
                    val documents = loadDocumentsInternal(
                        token = token,
                        patientId = patientId,
                        fallbackDocuments = emptyList()
                    )

                    _uiState.value = _uiState.value.copy(
                        isDeletingDocument = false,
                        documents = documents,
                        successMessage = "Document eliminat correctament"
                    )

                    loadPatient(context, patientId)
                } else {
                    _uiState.value = _uiState.value.copy(
                        isDeletingDocument = false,
                        errorMessage = when (response.code()) {
                            401 -> "Sessió caducada. Torna a iniciar sessió"
                            403 -> "No tens permisos per eliminar documents"
                            404 -> "No s'ha trobat aquest document"
                            else -> "No s'ha pogut eliminar el document"
                        }
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isDeletingDocument = false,
                    errorMessage = "Error inesperat: ${e.message}"
                )
            }
        }
    }

    fun downloadDocument(context: Context, document: DocumentDto) {
        val url = document.url?.trim()

        if (url.isNullOrBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Aquest document no té URL de descàrrega"
            )
            return
        }

        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            context.startActivity(intent)

            _uiState.value = _uiState.value.copy(
                successMessage = "Obrint document"
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "No s'ha pogut obrir el document: ${e.message}"
            )
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    private suspend fun loadDocumentsInternal(
        token: String,
        patientId: Long,
        fallbackDocuments: List<DocumentDto>
    ): List<DocumentDto> {
        return try {
            val documentsResponse = withContext(Dispatchers.IO) {
                RetrofitClient.documentApi.getDocumentsByPatient(
                    token = token,
                    patientId = patientId
                )
            }

            if (documentsResponse.isSuccessful) {
                documentsResponse.body().orEmpty().filter { it.active != false }
            } else {
                fallbackDocuments.filter { it.active != false }
            }
        } catch (e: Exception) {
            fallbackDocuments.filter { it.active != false }
        }
    }

    private suspend fun uploadDocumentsInternal(
        context: Context,
        token: String,
        patientId: Long,
        uris: List<Uri>
    ): UploadDocumentsResult {
        var uploadedCount = 0
        val failedMessages = mutableListOf<String>()

        uris.forEach { uri ->
            val fileName = sanitizePdfFileName(
                getFileName(context, uri).ifBlank {
                    "document-${System.currentTimeMillis()}.pdf"
                }
            )

            val mimeType = context.contentResolver.getType(uri).orEmpty()

            if (mimeType.isNotBlank() && mimeType != "application/pdf") {
                failedMessages.add("$fileName no és un PDF vàlid")
                return@forEach
            }

            val filePart = createPdfMultipartPart(
                context = context,
                uri = uri,
                fileName = fileName
            )

            val nameBody = fileName
                .removeSuffix(".pdf")
                .toRequestBody("text/plain".toMediaTypeOrNull())

            val typeBody = "OTHER".toRequestBody("text/plain".toMediaTypeOrNull())
            val notesBody = "".toRequestBody("text/plain".toMediaTypeOrNull())

            val response = RetrofitClient.documentApi.uploadDocument(
                token = token,
                patientId = patientId,
                file = filePart,
                name = nameBody,
                documentType = typeBody,
                notes = notesBody
            )

            if (response.isSuccessful) {
                uploadedCount++
            } else {
                val errorBody = response.errorBody()?.string().orEmpty()
                failedMessages.add(
                    if (errorBody.isNotBlank()) {
                        "No s'ha pogut pujar $fileName: ${response.code()} - $errorBody"
                    } else {
                        "No s'ha pogut pujar $fileName: error ${response.code()}"
                    }
                )
            }
        }

        return UploadDocumentsResult(
            uploadedCount = uploadedCount,
            failedMessages = failedMessages
        )
    }

    private fun createPdfMultipartPart(
        context: Context,
        uri: Uri,
        fileName: String
    ): MultipartBody.Part {
        val inputStream = context.contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("No s'ha pogut obrir el fitxer")

        val bytes = inputStream.use { it.readBytes() }

        val requestBody: RequestBody = bytes.toRequestBody(
            "application/pdf".toMediaTypeOrNull()
        )

        return MultipartBody.Part.createFormData(
            name = "file",
            filename = fileName,
            body = requestBody
        )
    }

    private fun getFileName(context: Context, uri: Uri): String {
        var result = ""

        if (uri.scheme == "content") {
            val cursor: Cursor? = context.contentResolver.query(
                uri,
                null,
                null,
                null,
                null
            )

            cursor.use {
                if (it != null && it.moveToFirst()) {
                    val index = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (index >= 0) {
                        result = it.getString(index).orEmpty()
                    }
                }
            }
        }

        if (result.isBlank()) {
            result = uri.lastPathSegment.orEmpty()
        }

        return result
    }

    private fun sanitizePdfFileName(value: String): String {
        val cleanName = value
            .trim()
            .replace(Regex("[^A-Za-z0-9._ -]"), "_")
            .ifBlank { "document-${System.currentTimeMillis()}" }

        return if (cleanName.endsWith(".pdf", ignoreCase = true)) {
            cleanName
        } else {
            "$cleanName.pdf"
        }
    }
}

private data class UploadDocumentsResult(
    val uploadedCount: Int,
    val failedMessages: List<String>
)