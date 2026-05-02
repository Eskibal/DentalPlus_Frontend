package com.example.dentalplus_frontend.network

import com.example.dentalplus_frontend.model.AppointmentCreateRequest
import com.example.dentalplus_frontend.model.BackendAppointmentDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface AppointmentApi {

    @GET("appointment")
    suspend fun getAppointments(
        @Header("Authorization") token: String,
        @Query("date") date: String? = null,
        @Query("patientId") patientId: Long? = null,
        @Query("dentistId") dentistId: Long? = null,
        @Query("boxId") boxId: Long? = null
    ): Response<List<BackendAppointmentDto>>

    @POST("appointment")
    suspend fun createAppointment(
        @Header("Authorization") token: String,
        @Body request: AppointmentCreateRequest
    ): Response<BackendAppointmentDto>

    @DELETE("appointment/{id}")
    suspend fun deleteAppointment(
        @Header("Authorization") token: String,
        @Path("id") appointmentId: Long
    ): Response<String>
}