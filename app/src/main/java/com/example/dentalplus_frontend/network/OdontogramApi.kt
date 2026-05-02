package com.example.dentalplus_frontend.network

import com.example.dentalplus_frontend.model.DentalSurfaceMarkBackendDto
import com.example.dentalplus_frontend.model.DentalSurfaceMarkRequest
import com.example.dentalplus_frontend.model.OdontogramBackendDto
import com.example.dentalplus_frontend.model.OdontogramViewModeRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface OdontogramApi {

    @POST("patient/{patientId}/odontogram")
    suspend fun createOdontogram(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long
    ): Response<OdontogramBackendDto>

    @GET("patient/{patientId}/odontogram")
    suspend fun getOdontogramByPatient(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long
    ): Response<OdontogramBackendDto>

    @PUT("patient/{patientId}/odontogram/view-mode")
    suspend fun updateViewModeByPatient(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long,
        @Body request: OdontogramViewModeRequest
    ): Response<OdontogramBackendDto>

    @GET("patient/{patientId}/odontogram/piece/{pieceNumber}/surface/{surfaceType}/mark")
    suspend fun getSurfaceMarks(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long,
        @Path("pieceNumber") pieceNumber: Int,
        @Path("surfaceType") surfaceType: String
    ): Response<List<DentalSurfaceMarkBackendDto>>

    @POST("patient/{patientId}/odontogram/piece/{pieceNumber}/surface/{surfaceType}/mark")
    suspend fun createSurfaceMark(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long,
        @Path("pieceNumber") pieceNumber: Int,
        @Path("surfaceType") surfaceType: String,
        @Body request: DentalSurfaceMarkRequest
    ): Response<DentalSurfaceMarkBackendDto>

    @DELETE("patient/odontogram/mark/{markId}")
    suspend fun deleteSurfaceMark(
        @Header("Authorization") token: String,
        @Path("markId") markId: Long
    ): Response<String>
}