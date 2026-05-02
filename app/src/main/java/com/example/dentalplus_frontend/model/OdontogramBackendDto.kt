package com.example.dentalplus_frontend.model

data class OdontogramBackendDto(
    val id: Long?,
    val patientId: Long?,
    val viewMode: String?,
    val pieces: List<DentalPieceBackendDto>?
)

data class DentalPieceBackendDto(
    val id: Long?,
    val pieceNumber: Int?,
    val pieceKind: String?,
    val states: List<DentalPieceStateBackendDto>?,
    val surfaces: List<DentalSurfaceBackendDto>?
)

data class DentalPieceStateBackendDto(
    val id: Long?,
    val stateType: String?,
    val active: Boolean?,
    val notes: String?
)

data class DentalSurfaceBackendDto(
    val id: Long?,
    val surfaceType: String?,
    val notes: String?,
    val marks: List<DentalSurfaceMarkBackendDto>?
)

data class DentalSurfaceMarkBackendDto(
    val id: Long?,
    val markType: String?,
    val markState: String?,
    val active: Boolean?,
    val notes: String?
)

data class DentalSurfaceMarkRequest(
    val markType: String,
    val markState: String,
    val notes: String? = null,
    val active: Boolean = true
)

data class OdontogramViewModeRequest(
    val viewMode: String
)