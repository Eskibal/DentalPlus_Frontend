package com.example.dentalplus_frontend.model

data class AvailabilityDto(
    val dentists: List<AvailableDentistDto>?,
    val boxes: List<AvailableBoxDto>?
)

data class AvailableDentistDto(
    val id: Long?,
    val fullName: String?,
    val speciality: String?
)

data class AvailableBoxDto(
    val id: Long?,
    val name: String?
)