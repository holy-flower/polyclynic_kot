package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.polyclynic_kot.server.DoctorResponse

class DoctorInfoFragment : Fragment() {
    private lateinit var doctor: DoctorResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doctor = arguments?.getParcelable<DoctorResponse>("DOCTOR_KEY")!!
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.doctor_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvNameDoc).text = "ФИО: ${doctor.name}"
        view.findViewById<TextView>(R.id.tvSpecializationDoc).text = "Специализация: ${doctor.specialization}"
        view.findViewById<TextView>(R.id.tvPhone).text = "Контактный телефон: ${doctor.phone}"
        view.findViewById<TextView>(R.id.tvLicenseDoc).text = "Номер лицензии: ${doctor.license}"
        view.findViewById<TextView>(R.id.tvEmailDoc).text = "Почта: ${doctor.emailDoc}"


    }

    companion object {
        fun newInstance(doctor: DoctorResponse): DoctorInfoFragment {
            return DoctorInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("DOCTOR_KEY", doctor)
                }
            }
        }
    }
}