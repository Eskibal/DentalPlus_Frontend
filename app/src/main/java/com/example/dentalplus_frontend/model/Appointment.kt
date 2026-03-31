package com.example.dentalplus_frontend.model

import java.time.LocalDate

data class Appointment(
    val start: String,
    val end: String,
    val patient: String,
    val doctor: String,
    val type: String,
    val date: LocalDate
)
