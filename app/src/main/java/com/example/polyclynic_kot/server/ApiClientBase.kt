package com.example.polyclynic_kot.server

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClientBase {
    private const val BASE_URL = "http://10.0.2.2:8080/"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val authApi: AuthApi = retrofit.create(AuthApi::class.java)
}