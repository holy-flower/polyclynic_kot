package com.example.polyclynic_kot

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.patient_start)

        val etLogin = findViewById<EditText>(R.id.etEmailPat)
        val etPassword = findViewById<EditText>(R.id.etPasswordPat)

        val bLogInAsDoc = findViewById<Button>(R.id.LogInAsDoc)
        bLogInAsDoc.setOnClickListener {
            val intent = Intent(this, StartDoctor::class.java)
            startActivity(intent)
        }

        val bLogIn = findViewById<Button>(R.id.LogInPat)
        bLogIn.setOnClickListener {


            val intent = Intent(this, PatientActivity::class.java)
            startActivity(intent)
        }

        val bRegister = findViewById<Button>(R.id.RegisterPat)
        bRegister.setOnClickListener {
            val intent = Intent(this, PatientRegistration::class.java)
            startActivity(intent)
        }
    }
}