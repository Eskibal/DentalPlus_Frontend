package com.example.dentalplus_frontend.model

data class LoginResponse(
    val token: String,
    val userId: Long,
    val profile: ProfileDto?
)

data class ProfileDto(
    val id: Long?,
    val username: String?,
    val active: Boolean?,
    val themePreference: String?,
    val languagePreference: String?,
    val notes: String?,
    val person: PersonDto?,
    val roles: List<RoleDto>?
)

data class PersonDto(
    val id: Long?,
    val name: String?,
    val firstSurname: String?,
    val secondSurname: String?,
    val birthDate: String?,
    val gender: String?,
    val email: String?,
    val phonePrefix: String?,
    val phoneNumber: String?,
    val address: String?,
    val city: String?,
    val profileImage: String?,
    val notes: String?
)

data class RoleDto(
    val roleType: String?,
    val roleId: Long?,
    val clinicId: Long?,
    val clinicName: String?,
    val active: Boolean?
)