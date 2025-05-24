package com.example.polyclynic_kot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.ContactsContract
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polyclynic_kot.MainActivity
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.AuthResponse
import com.example.polyclynic_kot.server.DoctorAuthResponse
import com.example.polyclynic_kot.server.DoctorLoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StartDoctor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_start)

        val etEmail = findViewById<EditText>(R.id.etEmailDoc)
        val etPassword = findViewById<EditText>(R.id.etPasswordDoc)

        val bLogInDoc = findViewById<Button>(R.id.LogInDoc)
        bLogInDoc.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginDoctor(email, password)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        val bRegister = findViewById<Button>(R.id.RegisterDoc)
        bRegister.setOnClickListener {
            val intent = Intent(this, DoctorRegistration::class.java)
            startActivity(intent)
        }

        val bLogInAsPat = findViewById<Button>(R.id.LogInAsPat)
        bLogInAsPat.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val imageView = findViewById<ImageView>(R.id.imageView)

        imageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun loginDoctor(email: String, password: String) {
        val call = ApiClientBase.authApi.loginDoctor(DoctorLoginRequest(emailDoc = email, passwordDoc = password))
        call.enqueue(object : Callback<DoctorAuthResponse> {
            override fun onResponse(call: Call<DoctorAuthResponse>, response: Response<DoctorAuthResponse>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@StartDoctor, "Успешный вход", Toast.LENGTH_SHORT).show()

                    val doctorId = response.body()!!.doctorId
                    val sharedPref = getSharedPreferences("doctor_session", Context.MODE_PRIVATE)
                    sharedPref.edit().putLong("DOCTOR_ID", doctorId).apply()

                    val intent = Intent(this@StartDoctor, DoctorActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@StartDoctor, "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<DoctorAuthResponse>, t: Throwable)  {
                Toast.makeText(this@StartDoctor, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}