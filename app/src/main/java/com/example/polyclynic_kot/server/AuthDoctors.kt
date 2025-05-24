package com.example.polyclynic_kot.server

data class DoctorLoginRequest(
    val emailDoc: String,
    val passwordDoc: String
)

data class DoctorRegisterRequest(
    val emailDoc: String,
    val name: String,
    val specialization: String,
    val license: String,
    val phone: String,
    val passwordDoc: String
)

data class DoctorAuthResponse(
    val doctorId: Long,
    val token: String
)