package com.example.polyclynic_kot.server

data class LoginRequest (
    val email: String,
    val password: String
)

data class RegisterRequest (
    val password: String,
    val email: String,
    val username: String,
    val datetime: String,
    val policy: String,
    val passport: String,
    val registerPlace: String
)

data class AuthResponse (
    val token: String
)