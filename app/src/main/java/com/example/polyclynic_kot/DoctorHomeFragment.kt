package com.example.polyclynic_kot

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.UserResponse
import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import com.example.polyclynic_kot.server.appointment.PatientAppointment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorHomeFragment : Fragment() {
    private var appointments = mutableListOf<PatientAppointment>()
    private var filteredAppointments = mutableListOf<PatientAppointment>()
    private lateinit var adapter: ListPatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView(view)
        setupSearch(view)
        setupSearchButton(view)
        loadAppointments()
    }

    private fun setupRecyclerView(view: View) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.patList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ListPatAdapter(filteredAppointments) { appointment ->
            appointment.user?.let { user ->
                parentFragmentManager.beginTransaction()
                    .replace(R.id.content_frame_doc, PatientInfoFragment.newInstance
                        (user, appointment.appointment.doctorId, appointment.appointment.date, appointment.appointment.time))
                    .addToBackStack(null)
                    .commit()
            }
        }
        recyclerView.adapter = adapter
    }

    private fun setupSearch(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchPat)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false

            override fun onQueryTextChange(newText: String?): Boolean {
                filterAppointments(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupSearchButton(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchPat)
        val button = view.findViewById<View>(R.id.bSearchPat)

        button.setOnClickListener {
            val query = searchView.query.toString().trim()
            filterAppointments(query)
        }
    }

    private fun filterAppointments(query: String) {
        val lowerQuery = query.lowercase()

        filteredAppointments.clear()
        filteredAppointments.addAll(
            appointments.filter {
                val name = it.user?.username?.lowercase().orEmpty()
                val email = it.user?.email?.lowercase().orEmpty()
                name.contains(lowerQuery) || email.contains(lowerQuery)
            }
        )
        adapter.notifyDataSetChanged()

        if (filteredAppointments.isEmpty()) {
            context?.let {
                Toast.makeText(it, "Пациент не найден", Toast.LENGTH_SHORT).show()
            }
        }
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

        appointments.clear()
        val total = appointmentsList.size
        var completed = 0

        for (appointment in appointmentsList) {
            ApiClientBase.authApi.getUserById(appointment.userId).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    val user = response.body()
                    appointments.add(PatientAppointment(appointment, user))
                    completed++
                    if (completed == total) {
                        //adapter.updateList(appointments)
                        filteredAppointments.clear()
                        filteredAppointments.addAll(appointments)
                        adapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    appointments.add(PatientAppointment(appointment, null))
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