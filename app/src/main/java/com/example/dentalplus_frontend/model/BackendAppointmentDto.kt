package com.example.dentalplus_frontend.model

data class BackendAppointmentDto(
    val id: Long?,
    val boxId: Long?,
    val boxName: String?,
    val dentistId: Long?,
    val dentistName: String?,
    val patientId: Long?,
    val patientName: String?,
    val startDateTime: String?,
    val endDateTime: String?,
    val status: String?,
    val notes: String?,
    val active: Boolean?
)