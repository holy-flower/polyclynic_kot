package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class AppointmentFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_appointment, container, false)

        val bRecording = view.findViewById<Button>(R.id.bConfirmRec)
        bRecording.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame_pat, PatientHomeFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}