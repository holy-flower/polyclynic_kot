package com.example.polyclynic_kot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.TextView
import android.widget.Toast
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.DoctorResponse
import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import com.example.polyclynic_kot.server.appointment.CreateAppointmentRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AppointmentFragment : Fragment() {
    private var selectedDate: String? = null
    private var selectedTime: String? = null

    private val doctorId: Long by lazy {
        arguments?.getLong("DOCTOR_ID", -1L) ?: -1L
    }

    private val currentUserId: Long by lazy {
        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        prefs.getLong("USER_ID", -1L)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (doctorId == -1L) {
            Toast.makeText(context, "Ошибка: доктор не выбран", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }
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

        if (doctorId == -1L) {
            Toast.makeText(context, "Ошибка: доктор не выбран", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
            return
        }

        loadDoctorDetails(doctorId)
        val context = requireContext()
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val tvTime = view.findViewById<TextView>(R.id.tvTime)

        view.findViewById<Button>(R.id.bSelectDate).setOnClickListener {
            val datePicker = android.app.DatePickerDialog(context)
            datePicker.setOnDateSetListener { _, year, month, dayOfMonth ->
                selectedDate = "%04d-%02d-%02d".format(year, month + 1, dayOfMonth)
                tvDate.text = selectedDate
            }
            datePicker.show()
        }

        view.findViewById<Button>(R.id.bSelectTime).setOnClickListener {
            val timePicker = android.app.TimePickerDialog(context, { _, hourOfDay, minute ->
                selectedTime = "%02d:%02d".format(hourOfDay, minute)
                tvTime.text = selectedTime
            }, 9, 0, true)
            timePicker.show()
        }

        view.findViewById<Button>(R.id.bConfirmRec).setOnClickListener {
            if (selectedDate.isNullOrBlank() || selectedTime.isNullOrBlank()) {
                Toast.makeText(context, "Выберите дату и время", Toast.LENGTH_SHORT).show()
            } else {
                createAppointment(selectedDate!!, selectedTime!!)
            }
        }

    }

    private fun loadDoctorDetails(doctorId: Long) {
        ApiClientBase.authApi.getDoctorById(doctorId).enqueue(object : Callback<DoctorResponse> {
            override fun onResponse(
                call: Call<DoctorResponse?>,
                response: Response<DoctorResponse?>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { doctor ->
                        view?.findViewById<TextView>(R.id.tvNameDoctor)?.text = doctor.name
                        view?.findViewById<TextView>(R.id.tvSpecializationDoctor)?.text = doctor.specialization
                    }
                } else {
                    val errorMessage = response.errorBody()?.string()
                    Toast.makeText(context, "Ошибка загрузки доктора: $errorMessage", Toast.LENGTH_LONG).show()
                    println("Ошибка при загрузке доктора: код ${response.code()}, тело: $errorMessage")
                }
            }

            override fun onFailure(call: Call<DoctorResponse?>, t: Throwable) {
                Toast.makeText(context, "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun createAppointment(date: String, time: String) {
        val userId = currentUserId
        if (userId == -1L) {
            Toast.makeText(context, "Ошибка: пользователь не авторизован", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClientBase.authApi.createAppointment(
            CreateAppointmentRequest(
                doctorId = doctorId,
                userId = userId,
                date = date,
                time = time
            )
        ).enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Запись создана на $date $time", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Не удалось создать запись", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Ошибка записи: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance(doctorID: Long): AppointmentFragment {
            return AppointmentFragment().apply {
                arguments = Bundle().apply {
                    putLong("DOCTOR_ID", doctorID)
                }
            }
        }
    }
}