package com.example.polyclynic_kot.server.appointment

import com.example.polyclynic_kot.server.UserResponse

data class AppointmentResponse (
    val doctorId: Long,
    val userId: Long,
    val date: String,
    val time: String
)

data class PatientAppointment(
    val appointment: AppointmentResponse,
    val user: UserResponse?
)