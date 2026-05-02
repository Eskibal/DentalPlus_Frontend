package com.example.dentalplus_frontend.network

import com.example.dentalplus_frontend.model.LoginRequest
import com.example.dentalplus_frontend.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("user/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}