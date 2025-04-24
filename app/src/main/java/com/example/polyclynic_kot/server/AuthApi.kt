package com.example.polyclynic_kot.server

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("login")
    suspend fun login (@Body login: LoginRequest): Call<AuthResponse>

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Call<AuthResponse>
}