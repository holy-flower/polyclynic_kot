package com.example.polyclynic_kot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.AuthResponse
import com.example.polyclynic_kot.server.LoginRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_start)

        val etEmail = findViewById<EditText>(R.id.etEmailPat)
        val etPassword = findViewById<EditText>(R.id.etPasswordPat)

        val bLogInAsDoc = findViewById<Button>(R.id.LogInAsDoc)
        bLogInAsDoc.setOnClickListener {
            val intent = Intent(this, StartDoctor::class.java)
            startActivity(intent)
        }

        val bLogIn = findViewById<Button>(R.id.LogInPat)
        bLogIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            }
        }

        val bRegister = findViewById<Button>(R.id.RegisterPat)
        bRegister.setOnClickListener {
            val intent = Intent(this, PatientRegistration::class.java)
            startActivity(intent)
        }
    }

    private fun loginUser(email: String, password: String) {
        val call = ApiClientBase.authApi.login(LoginRequest(email, password))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                Log.d("LOGIN", "Response code: ${response.code()}")
                Log.d("LOGIN", "Response body: ${response.body()}")
                Log.d("LOGIN", "Error body: ${response.errorBody()?.string()}")

                if (response.isSuccessful) {
                    Toast.makeText(this@MainActivity, "Успешный вход", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@MainActivity, PatientActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@MainActivity, "Ошибка входа", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}