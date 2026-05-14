package com.example.dentalplus_frontend.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    /*
     * Render puede tardar bastante en responder si el backend está dormido.
     * Por eso dejamos timeouts más amplios que los 20 segundos originales.
     */
    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .callTimeout(75, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(ApiConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }

    val appointmentApi: AppointmentApi by lazy {
        retrofit.create(AppointmentApi::class.java)
    }

    val patientApi: PatientApi by lazy {
        retrofit.create(PatientApi::class.java)
    }

    val odontogramApi: OdontogramApi by lazy {
        retrofit.create(OdontogramApi::class.java)
    }

    val documentApi: DocumentApi by lazy {
        retrofit.create(DocumentApi::class.java)
    }
}