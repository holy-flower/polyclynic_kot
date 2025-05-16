package com.example.polyclynic_kot.server

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    fun login (@Body login: LoginRequest): Call<AuthResponse>

    @POST("register")
    fun register(@Body request: RegisterRequest): Call<AuthResponse>

    @POST("doctors/login")
    fun loginDoctor(@Body login: DoctorLoginRequest): Call<DoctorAuthResponse>

    @POST("doctors/register")
    fun registerDoctor(@Body request: DoctorRegisterRequest): Call<DoctorAuthResponse>
}