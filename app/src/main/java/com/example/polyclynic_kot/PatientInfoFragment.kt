package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.UserResponse
import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PatientInfoFragment : Fragment() {
    private lateinit var user: UserResponse
    private var doctorId: Long = -1
    private lateinit var date: String
    private lateinit var time: String

    companion object {
        fun newInstance(user: UserResponse, doctorId: Long, date: String, time: String): PatientInfoFragment {
            return PatientInfoFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("USER_DATA", user)
                    putLong("DOCTOR_ID", doctorId)
                    putString("DATE", date)
                    putString("TIME", time)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable("USER_DATA")!!
            doctorId = it.getLong("DOCTOR_ID")
            date = it.getString("DATE")!!
            time = it.getString("TIME")!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvNamePatient).text = "ФИО: ${user.username}"
        view.findViewById<TextView>(R.id.tvBirthdayPatient).text = "Дата рождения: ${user.datetime}"
        view.findViewById<TextView>(R.id.tvPolicyPatient).text = "Полис: ${user.policy}"
        view.findViewById<TextView>(R.id.tvPassportPatient).text = "Паспортные данные: ${user.passport}"
        view.findViewById<TextView>(R.id.tvRegistrationPatient).text = "Место регистрации: ${user.registerPlace}"

        val notesTv = view.findViewById<TextView>(R.id.tvMedicalHistory)

        ApiClientBase.authApi.getAppointmentByKeys(doctorId, user.user_id, date, time)
            .enqueue(object : Callback<AppointmentResponse> {
                override fun onResponse(
                    call: Call<AppointmentResponse?>,
                    response: Response<AppointmentResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val notes = response.body()!!.notes
                        notesTv.text = "История болезни: ${notes.ifEmpty { "Нет данных" }}"
                    } else {
                        notesTv.text = "История болезни: не найдена"
                    }
                }

                override fun onFailure(call: Call<AppointmentResponse?>, t: Throwable) {
                    notesTv.text = "Ошибка загрузки истории болезни"
                }
            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_info, container, false)
    }
}