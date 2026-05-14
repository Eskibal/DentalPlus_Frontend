package com.example.dentalplus_frontend.network

import com.example.dentalplus_frontend.model.DocumentDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface DocumentApi {

    @GET("document/patient/{patientId}")
    suspend fun getDocumentsByPatient(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long
    ): Response<List<DocumentDto>>

    @Multipart
    @POST("document/patient/{patientId}")
    suspend fun uploadDocument(
        @Header("Authorization") token: String,
        @Path("patientId") patientId: Long,
        @Part file: MultipartBody.Part,
        @Part("name") name: RequestBody,
        @Part("documentType") documentType: RequestBody,
        @Part("notes") notes: RequestBody?
    ): Response<DocumentDto>

    @DELETE("document/{id}")
    suspend fun deleteDocument(
        @Header("Authorization") token: String,
        @Path("id") documentId: Long
    ): Response<String>
}