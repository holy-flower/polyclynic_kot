package com.example.polyclynic_kot.server

data class DoctorResponse(
    val emailDoc: String,
    val name: String,
    val specialization: String,
    val license: String,
    val phone: String
) {
    // Для отладки можно переопределить toString()
    override fun toString(): String {
        return "Doctor(name='$name', email='$emailDoc', specialization='$specialization')"
    }
}