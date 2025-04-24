package com.example.polyclynic_kot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class DoctorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_doctor)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_doc)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content_frame_doc, DoctorHomeFragment())
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.home_doc -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_doc, DoctorHomeFragment())
                        .commit()
                    true
                }

                R.id.techniques -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_doc, DoctorTechniquesFragment())
                        .commit()
                    true
                }

                R.id.settins_doc -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_doc, SettingsDocFragment())
                        .commit()
                    true
                }

                else -> false
            }
        }
    }
}