package com.example.dentalplus_frontend.network

import com.example.dentalplus_frontend.model.ProfileDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

interface UserApi {

    @GET("user/me")
    suspend fun getMyProfile(
        @Header("Authorization") token: String
    ): Response<ProfileDto>
}