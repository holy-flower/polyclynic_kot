package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class DoctorInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.doctor_info, container, false)

        val bAppointment = view.findViewById<Button>(R.id.bAppointment)
        bAppointment.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame_pat, AppointmentFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}