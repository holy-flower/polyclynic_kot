package com.example.polyclynic_kot

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class StartDoctor : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.doctor_start)

        val bRegister = findViewById<Button>(R.id.RegisterDoc)
        bRegister.setOnClickListener {
            val intent = Intent(this, DoctorRegistration::class.java)
            startActivity(intent)
        }

        val bLogInDoc = findViewById<Button>(R.id.LogInDoc)
        bLogInDoc.setOnClickListener {
            val intent = Intent(this, DoctorActivity::class.java)
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

}