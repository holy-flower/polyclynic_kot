package com.example.polyclynic_kot.server.appointment

data class AppointmentResponse (
    val doctorId: Long,
    val userId: Long,
    val date: Long,
    val time: Long
)