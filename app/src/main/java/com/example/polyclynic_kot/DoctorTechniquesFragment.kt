package com.example.polyclynic_kot

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polyclynic_kot.DoctorRegistration
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.UserResponse
import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import com.example.polyclynic_kot.server.appointment.PatientAppointment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDateTime


class DoctorTechniquesFragment : Fragment() {
    private var appointments = mutableListOf<PatientAppointment>()
    private lateinit var adapter: ListPatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctor_techniques, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        loadAppointments()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.patListMed)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ListPatAdapter(mutableListOf()) { appointment ->
            appointment.user?.let { user ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.content_frame_doc, AddMedicalHistoryPatFragment.newInstance(
                        user, appointment.appointment.doctorId, appointment.appointment.date, appointment.appointment.time))
                    .addToBackStack(null)
                    .commit()
            }
        }
        recyclerView.adapter = adapter
    }

    private fun loadAppointments() {
        val doctorId = getCurrentDoctorId()
        println("DEBUG: Loading appointments for doctor ID: $doctorId")

        if (doctorId == -1L) {
            Toast.makeText(requireContext(), "Не удалось получить ID доктора", Toast.LENGTH_LONG).show()
            return
        }

        ApiClientBase.authApi.getDoctorAppointments(doctorId).enqueue(object : Callback<List<AppointmentResponse>> {
            override fun onResponse(
                call: Call<List<AppointmentResponse>?>,
                response: Response<List<AppointmentResponse>?>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val appointmentsList = response.body()!!
                    Log.d("APPOINTMENTS", "Получено записей: ${appointmentsList.size}")
                    appointmentsList.forEach { Log.d("APPOINTMENT", "appointment.userId=${it.userId}") }
                    loadUsersForAppointments(appointmentsList)
                } else {
                    Toast.makeText(requireContext(), "Нет записей на приём", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AppointmentResponse>?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки приёмов: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUsersForAppointments(appointmentsList: List<AppointmentResponse>) {
        if (appointmentsList.isEmpty()) {
            adapter.updateList(emptyList())
            return
        }

        val now = LocalDateTime.now()

        val upcomingAppointments = appointmentsList.filter {
            val appointmentsDateTime = LocalDateTime.parse("${it.date}T${it.time}")
            appointmentsDateTime.isAfter(now)
        }

        if (upcomingAppointments.isEmpty()) {
            adapter.updateList(emptyList())
            return
        }

        appointments.clear()
        val total = upcomingAppointments.size
        var completed = 0

        for (appointment in upcomingAppointments) {
            ApiClientBase.authApi.getUserById(appointment.userId).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    val user = response.body()
                    appointments.add(PatientAppointment(appointment, user))
                    completed++
                    if (completed == total) {
                        adapter.updateList(appointments)
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    //appointments.add(PatientAppointment(appointment, null))
                    completed++
                    if (completed == total) {
                        adapter.updateList(appointments)
                    }
                }
            })
        }
    }

    private fun getCurrentDoctorId(): Long {
        val prefs = requireContext().getSharedPreferences("doctor_session", Context.MODE_PRIVATE)
        val id = prefs.getLong("DOCTOR_ID", -1L)
        if (id == -1L) {
            Toast.makeText(requireContext(), "Доктор не авторизован", Toast.LENGTH_SHORT).show()
        }
        return id
    }
}