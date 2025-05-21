package com.example.polyclynic_kot.server

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {
    @POST("users/login")
    fun login (@Body login: LoginRequest): Call<AuthResponse>

    @POST("users/register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("doctors/login")
    fun loginDoctor(@Body login: DoctorLoginRequest): Call<DoctorAuthResponse>

    @POST("doctors/register")
    fun registerDoctor(@Body request: DoctorRegisterRequest): Call<DoctorAuthResponse>

    @GET("doctors")
    fun getDoctors(): Call<List<DoctorResponse>>

    @GET("doctors/by-specialization")
    fun getDoctorsBySpecialization(@Query("specialization") specialization: String): Call<List<DoctorResponse>>
}