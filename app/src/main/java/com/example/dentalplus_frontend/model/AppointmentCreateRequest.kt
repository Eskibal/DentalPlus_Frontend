package com.example.dentalplus_frontend.model

data class AppointmentCreateRequest(
    val boxId: Long,
    val dentistId: Long,
    val patientId: Long,
    val startDateTime: String,
    val endDateTime: String,
    val status: String = "SCHEDULED",
    val notes: String? = null,
    val active: Boolean = true
)