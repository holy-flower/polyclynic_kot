package com.example.polyclynic_kot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.UserResponse
import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicalCardPatFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_medicalcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = getCurrentUserId()
        if (userId == -1L) return

        ApiClientBase.authApi.getUserById(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    view.findViewById<TextView>(R.id.tvPatientName).text = "ФИО: ${user.username}"
                    view.findViewById<TextView>(R.id.tvPatientBirthday).text = "Дата рождения: ${user.datetime}"
                    view.findViewById<TextView>(R.id.tvPatientPolicy).text = "Полис: ${user.policy}"
                    view.findViewById<TextView>(R.id.tvPatientPassport).text = "Паспортные данные: ${user.passport}"
                    view.findViewById<TextView>(R.id.tvPatientRegistration).text = "Место регистрации: ${user.registerPlace}"
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })

        val tvMedicalHistory = view.findViewById<TextView>(R.id.tvMedHistory)
        ApiClientBase.authApi.getAppointmentsByUserId(userId).enqueue(object : Callback<List<AppointmentResponse>> {
            override fun onResponse(
                call: Call<List<AppointmentResponse>?>,
                response: Response<List<AppointmentResponse>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val appointments = response.body()!!
                    if (appointments.isNotEmpty()) {
                        val historyText = appointments.mapIndexed { index, appointment ->
                            "${index + 1}) ${appointment.date} ${appointment.time} - ${appointment.notes.ifEmpty { "Нет записи" }}"
                        }.joinToString("\n")

                        tvMedicalHistory.text = "История болезни: \n$historyText"
                    } else {
                        tvMedicalHistory.text = "История болезни: нет записей"
                    }
                } else {
                    tvMedicalHistory.text = "Ошибка загрузки истории болезни"
                }
            }

            override fun onFailure(call: Call<List<AppointmentResponse>?>, t: Throwable) {
                tvMedicalHistory.text = "Ошибка при подключении к серверу: ${t.message}"
            }
        })
    }

    private fun getCurrentUserId(): Long {
        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return prefs.getLong("USER_ID", -1L)
    }
}