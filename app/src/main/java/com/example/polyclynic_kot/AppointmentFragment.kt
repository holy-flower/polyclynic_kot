package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.polyclynic_kot.server.DoctorResponse

class AppointmentFragment : Fragment() {
    private lateinit var doctor: DoctorResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doctor = arguments?.getParcelable("SELECTED_DOCTOR")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_appointment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvNameDoctor).text = doctor.name
        view.findViewById<TextView>(R.id.tvSpecializationDoctor).text = doctor.specialization

        view.findViewById<Button>(R.id.bConfirmRec).setOnClickListener {
            confirmAppointment()
        }
    }

    companion object {
        fun newInstance(doctor: DoctorResponse): AppointmentFragment {
            return AppointmentFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("SELECTED_DOCTOR", doctor)
                }
            }
        }
    }

    private fun confirmAppointment() {
        //Toast.makeText(AppointmentFragment(), "Успешная регистрация", Toast.LENGTH_SHORT).show()
    }
}