package com.example.polyclynic_kot.server

import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import com.example.polyclynic_kot.server.appointment.CreateAppointmentRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
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

    @GET("doctors/{id}")
    fun getDoctorById(@Path("id") id: Long): Call<DoctorResponse>

    @POST("appointments")
    fun createAppointment(@Body request: CreateAppointmentRequest): Call<Void>

    @GET("users/{id}")
    fun getUserById(@Path("id") id: Long): Call<UserResponse>

    @GET("appointments/doctor/{doctorId}")
    fun getDoctorAppointments(@Path("doctorId") doctorId: Long): Call<List<AppointmentResponse>>

    @PUT("appointments/notes")
    fun updateMedicalNotes(
        @Query("doctorId") doctorId: Long,
        @Query("userId") userId: Long,
        @Query("date") date: String,
        @Query("time") time: String,
        @Body notes: String
    ): Call<Void>

    @GET("appointments/by-keys")
    fun getAppointmentByKeys(
        @Query("doctorId") doctorId: Long,
        @Query("userId") userId: Long,
        @Query("date") date: String,
        @Query("time") time: String
    ): Call<AppointmentResponse>

    @GET("appointments/by-user/{userId}")
    fun getAppointmentsByUserId(@Path("userId") userId: Long): Call<List<AppointmentResponse>>
}