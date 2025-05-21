package com.example.polyclynic_kot.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DoctorResponse(
    val emailDoc: String,
    val name: String,
    val specialization: String,
    val license: String,
    val phone: String
) : Parcelable {
    // Для отладки можно переопределить toString()
    override fun toString(): String {
        return "Doctor(name='$name', email='$emailDoc', specialization='$specialization')"
    }
}