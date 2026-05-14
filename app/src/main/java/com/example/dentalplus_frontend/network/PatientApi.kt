package com.example.dentalplus_frontend.network

import com.example.dentalplus_frontend.model.BackendPatientDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PatientApi {

    @GET("patient")
    suspend fun getPatients(
        @Header("Authorization") token: String,
        @Query("search") search: String? = null
    ): Response<List<BackendPatientDto>>

    @GET("patient/{id}")
    suspend fun getPatientById(
        @Header("Authorization") token: String,
        @Path("id") patientId: Long
    ): Response<BackendPatientDto>

    @POST("patient")
    suspend fun createPatient(
        @Header("Authorization") token: String,
        @Body patient: BackendPatientDto
    ): Response<BackendPatientDto>

    @PUT("patient/{id}")
    suspend fun updatePatient(
        @Header("Authorization") token: String,
        @Path("id") patientId: Long,
        @Body patient: BackendPatientDto
    ): Response<BackendPatientDto>
}