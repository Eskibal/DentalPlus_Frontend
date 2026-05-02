package com.example.dentalplus_frontend.model

data class BackendPatientDto(
    val patientId: Long?,
    val userId: Long?,
    val clinicId: Long?,
    val clinicName: String?,
    val registrationDate: String?,
    val active: Boolean?,
    val notes: String?,
    val person: PersonDto?,
    val documents: List<DocumentDto>?
)

data class DocumentDto(
    val id: Long?,
    val patientId: Long?,
    val name: String?,
    val storagePath: String?,
    val url: String?,
    val mimeType: String?,
    val documentType: String?,
    val active: Boolean?,
    val notes: String?
)