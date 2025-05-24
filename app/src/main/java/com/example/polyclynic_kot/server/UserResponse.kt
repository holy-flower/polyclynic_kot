package com.example.polyclynic_kot.server

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserResponse (
    val user_id: Long,
    val email: String,
    val username: String,
    val datetime: String,
    val policy: String,
    val passport: String,
    val registerPlace: String

) : Parcelable {
    override fun toString(): String {
        return "User(id=$user_id, name='$username', email='$email')"
    }
}