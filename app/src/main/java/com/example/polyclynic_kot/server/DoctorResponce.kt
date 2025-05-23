package com.example.polyclynic_kot.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DoctorResponse(
    val doctor_id: Long,
    val emailDoc: String,
    val name: String,
    val specialization: String,
    val license: String,
    val phone: String
) : Parcelable {
    override fun toString(): String {
        return "Doctor(id=$doctor_id, name='$name', email='$emailDoc', specialization='$specialization')"
    }
}