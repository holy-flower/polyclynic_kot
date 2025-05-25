package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.polyclynic_kot.server.UserResponse

class PatientInfoFragment : Fragment() {
    private lateinit var user: UserResponse

    companion object {
        fun newInstance(user: UserResponse): PatientInfoFragment {
            return PatientInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("USER_DATA", user)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getParcelable("USER_DATA")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvNamePatient).text = "ФИО: ${user.username}"
        view.findViewById<TextView>(R.id.tvBirthdayPatient).text = "Дата рождения: ${user.datetime}"
        view.findViewById<TextView>(R.id.tvPolicyPatient).text = "Полис: ${user.policy}"
        view.findViewById<TextView>(R.id.tvPassportPatient).text = "Паспортные данные: ${user.passport}"
        view.findViewById<TextView>(R.id.tvRegistrationPatient).text = "Место регистрации: ${user.registerPlace}"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_info, container, false)
    }
}