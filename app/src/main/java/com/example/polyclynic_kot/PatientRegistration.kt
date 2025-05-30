package com.example.polyclynic_kot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.nfc.Tag
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.AuthResponse
import com.example.polyclynic_kot.server.RegisterRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class PatientRegistration : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_register)

        val etUserName = findViewById<EditText>(R.id.etNamePat)
        val etPassword = findViewById<EditText>(R.id.etPasswordPatReg)
        val etEmail = findViewById<EditText>(R.id.etEmailPatReg)
        val etBirthday = findViewById<EditText>(R.id.etBirthdayPat)
        val etPolicy = findViewById<EditText>(R.id.etPolicyPat)
        val etPassport = findViewById<EditText>(R.id.etPassportPat)
        val etRegisterPlace = findViewById<EditText>(R.id.etRegisterPat)

        val bRegisterPat = findViewById<Button>(R.id.bRegisterPat)
        bRegisterPat.setOnClickListener {
            val password = etPassword.text.toString()
            val userName = etUserName.text.toString()
            val email = etEmail.text.toString()
            val birthday = etBirthday.text.toString()
            val policy = etPolicy.text.toString()
            val passport = etPassport.text.toString()
            val registerPlace = etRegisterPlace.text.toString()

            if (userName.isBlank() || password.isBlank() || email.isBlank()
                || birthday.isBlank() || policy.isBlank() || passport.isBlank() || registerPlace.isBlank()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userName.trim().split("\\s+".toRegex()).size != 3) {
                Toast.makeText(this, "Введите ФИО (три слова)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!birthday.matches(Regex("""\d{2}/\d{2}/\d{4}"""))) {
                Toast.makeText(this, "Дата рождения должна быть в формате ДД/ММ/ГГГГ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!policy.matches(Regex("""\d{16}"""))) {
                Toast.makeText(this, "Полис должен содержать 16 цифр", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!passport.matches(Regex("""\d{2}\s\d{2}\s\d{6}"""))) {
                Toast.makeText(this, "Паспорт должен быть в формате: ХХ ХХ ХХХХХХ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            registerUser(userName, password, email, birthday, policy, passport, registerPlace)

        }

        val imageView = findViewById<ImageView>(R.id.imageView)

        imageView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun registerUser (userName: String, password: String, email: String,
                              birthday: String, policy: String, passport: String, registerPlace: String) {
        val registerRequest = RegisterRequest(password, email, userName, birthday, policy, passport, registerPlace)
        Log.d("PatientRegistration", "Register request: $registerRequest")

        val call = ApiClientBase.authApi.register(registerRequest)

        //val call = ApiClientBase.authApi.register(RegisterRequest(password, userName, email, birthday, policy, passport, registerPlace))
        call.enqueue(object : Callback<AuthResponse> {
            override fun onFailure(call: Call<AuthResponse>, t: Throwable) {
                Toast.makeText(this@PatientRegistration, "Ошибка сети: ${t.message}", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<AuthResponse>, response: Response<AuthResponse>) {
                if (response.isSuccessful) {
                    val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
                    sharedPref.edit().putLong("USER_ID", response.body()?.userId ?: -1).apply()

                    val intent = Intent(this@PatientRegistration, PatientActivity::class.java)
                    startActivity(intent)
                } else {
                    Log.e("PatientRegistration", "Registration error: ${response.code()} ${response.message()}")
                    Toast.makeText(this@PatientRegistration, "Ошибка в регистрации", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }
}