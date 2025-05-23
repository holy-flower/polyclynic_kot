package com.example.polyclynic_kot.server.appointment

data class CreateAppointmentRequest (
    val doctorId: Long,
    val userId: Long,
    val date: String,
    val time: String
)