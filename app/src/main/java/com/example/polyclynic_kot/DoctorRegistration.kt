package com.example.polyclynic_kot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polyclynic_kot.PatientRegistration
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.AuthResponse
import com.example.polyclynic_kot.server.DoctorAuthResponse
import com.example.polyclynic_kot.server.DoctorRegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorRegistration : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_register)

        val etEmail = findViewById<EditText>(R.id.etEmailRegDoc)
        val etName = findViewById<EditText>(R.id.etNameDoc)
        val etSpecialization = findViewById<EditText>(R.id.etSpecializationDoc)
        val etLicense = findViewById<EditText>(R.id.etLicenseDoc)
        val etPhone = findViewById<EditText>(R.id.etPhoneDoc)
        val etPassword = findViewById<EditText>(R.id.etPasswordRegDoc)

        val bRegisterDoc = findViewById<Button>(R.id.bRegisterDoc)
        bRegisterDoc.setOnClickListener {
            val email = etEmail.text.toString()
            val name = etName.text.toString()
            val specialization = etSpecialization.text.toString()
            val license = etLicense.text.toString()
            val phone = etPhone.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && name.isNotEmpty() && specialization.isNotEmpty()
                && license.isNotEmpty() && phone.isNotEmpty() && password.isNotEmpty()) {
                registerDoctor(email, name, specialization, license, phone, password)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        val imageView = findViewById<ImageView>(R.id.imageView)

        imageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun registerDoctor(email: String, name: String, specialization: String,
                               license: String, phone: String, password: String) {
        val registerRequestDoc = DoctorRegisterRequest(emailDoc = email, name = name, specialization = specialization, license = license, phone = phone, passwordDoc = password)
        Log.d("PatientRegistration", "Register request: $registerRequestDoc")

        val call = ApiClientBase.authApi.registerDoctor(registerRequestDoc)

        call.enqueue(object : Callback<DoctorAuthResponse> {
            override fun onFailure(call: Call<DoctorAuthResponse?>, t: Throwable) {
                Toast.makeText(this@DoctorRegistration, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<DoctorAuthResponse?>, response: Response<DoctorAuthResponse?>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@DoctorRegistration, DoctorActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e("PatientRegistration", "Registration error: ${response.code()} ${response.message()}")
                    Toast.makeText(this@DoctorRegistration, "Ошибка в регистрации", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}