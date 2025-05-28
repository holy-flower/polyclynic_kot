package com.example.polyclynic_kot

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatDelegate

class SettingsPatFragment : Fragment() {
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.settings_layout, container, false)

        val switchTheme = view.findViewById<Switch>(R.id.switchTheme)
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        return view
    }
}

/*
val bSearchMed = view.findViewById<Button>(R.id.bSearchMed)
        bSearchMed.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame_pat, AIFragment())
                .addToBackStack(null)
                .commit()
        }

<Button
        android:id="@+id/bSearchMed"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="144dp"
        android:text="@string/search_info_medicine"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />
 */