package com.example.polyclynic_kot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomnavigation.BottomNavigationView

class PatientActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation_pat)

        if(savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.content_frame_pat, PatientHomeFragment())
                .commit()
        }

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home_pat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_pat, PatientHomeFragment())
                        .commit()
                    true
                }

                R.id.nav_medcard_pat -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_pat, MedicalCardPatFragment())
                        .commit()

                    true
                }

                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.content_frame_pat, SettingsPatFragment())
                        .commit()

                    true
                }

                else -> false
            }
        }
    }
}