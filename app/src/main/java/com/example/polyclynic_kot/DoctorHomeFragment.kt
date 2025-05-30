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

    private val SEARCH_PREF = "search_history"
    private val SEARCH_KEY = "history_list"
    private val MAX_HISTORY_SIZE = 10
    private var searchHistory = mutableListOf<String>()
    private lateinit var historyAdapter: android.widget.ArrayAdapter<String>

    private lateinit var searchHistoryList: android.widget.ListView
    private lateinit var clearHistoryButton: View

    private lateinit var placeholderError: View
    private lateinit var placeholderNoResults: View
    private lateinit var retryButton: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_list, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        searchHistoryList = view.findViewById(R.id.searchHistoryList)
        clearHistoryButton = view.findViewById(R.id.clearHistoryButton)

        placeholderError = view.findViewById(R.id.placeholder_error_doc)
        placeholderNoResults = view.findViewById(R.id.placeholder_no_results_doc)
        retryButton = view.findViewById(R.id.button_retry_doc)

        retryButton.setOnClickListener {
            placeholderError.visibility = View.GONE
            loadAppointments()
        }

        loadSearchHistory()
        setupHistoryUI()

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

    private fun loadSearchHistory() {
        val prefs = requireContext().getSharedPreferences(SEARCH_PREF, Context.MODE_PRIVATE)
        searchHistory.clear()

        try {
            val savedString = prefs.getString(SEARCH_KEY, null)
            if (!savedString.isNullOrEmpty()) {
                searchHistory.addAll(savedString.split(",").filter { it.isNotBlank() })
            } else {
                val savedSet = prefs.getStringSet(SEARCH_KEY, emptySet())
                if (!savedSet.isNullOrEmpty()) {
                    searchHistory.addAll(savedSet)
                }
            }
        } catch (e: ClassCastException) {
            prefs.edit().remove(SEARCH_KEY).apply()
        }
    }

    private fun saveSearchQuery(query: String) {
        if (query.isBlank()) return

        searchHistory.remove(query)
        searchHistory.add(0, query)

        while (searchHistory.size > MAX_HISTORY_SIZE) {
            searchHistory.removeAt(searchHistory.lastIndex)
        }

        val prefs = requireContext().getSharedPreferences(SEARCH_PREF, Context.MODE_PRIVATE)
        prefs.edit().putString(SEARCH_KEY, searchHistory.joinToString(",")).apply()

        if (::historyAdapter.isInitialized) {
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun clearSearchHistory() {
        searchHistory.clear()
        val prefs = requireContext().getSharedPreferences(SEARCH_PREF, Context.MODE_PRIVATE)
        prefs.edit().remove(SEARCH_KEY).apply()

        if (::historyAdapter.isInitialized) {
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun setupHistoryUI() {
        historyAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, searchHistory)
        searchHistoryList.adapter = historyAdapter

        searchHistoryList.setOnItemClickListener { _, _, position, _ ->
            val selected = searchHistory[position]
            val searchView = view?.findViewById<SearchView>(R.id.etSearchPat)
            searchView?.setQuery(selected, true)
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
            hideSearchHistory()
        }
    }

    private fun showSearchHistory() {
        if (searchHistory.isNotEmpty()) {
            searchHistoryList.visibility = View.VISIBLE
            clearHistoryButton.visibility = View.VISIBLE
        }
    }

    private fun hideSearchHistory() {
        searchHistoryList.visibility = View.GONE
        clearHistoryButton.visibility = View.GONE
    }

    private fun setupSearch(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchPat)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchHistory.isNotEmpty()) {
                showSearchHistory()
            } else {
                hideSearchHistory()
            }
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    saveSearchQuery(it)
                    filterAppointments(it)
                    hideSearchHistory()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun setupSearchButton(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchPat)
        val button = view.findViewById<View>(R.id.bSearchPat)

        button.setOnClickListener {
            val query = searchView.query.toString().trim()
            if (query.isNotEmpty()) {
                saveSearchQuery(query)
                filterAppointments(query)
                hideSearchHistory()
            }
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

        placeholderNoResults.visibility = if (filteredAppointments.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun loadAppointments() {
        val doctorId = getCurrentDoctorId()
        println("DEBUG: Loading appointments for doctor ID: $doctorId")

        placeholderError.visibility = View.GONE
        placeholderNoResults.visibility = View.GONE

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
                    if (appointmentsList.isEmpty()) {
                        placeholderNoResults.visibility = View.VISIBLE
                    }
                    loadUsersForAppointments(appointmentsList)
                } else {
                    placeholderError.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Нет записей на приём", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<AppointmentResponse>?>, t: Throwable) {
                placeholderError.visibility = View.VISIBLE
                Toast.makeText(requireContext(), "Ошибка загрузки приёмов: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun loadUsersForAppointments(appointmentsList: List<AppointmentResponse>) {
        if (appointmentsList.isEmpty()) {
            filteredAppointments.clear()
            adapter.notifyDataSetChanged()
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
                        filteredAppointments.clear()
                        filteredAppointments.addAll(appointments)
                        adapter.notifyDataSetChanged()
                        placeholderNoResults.visibility = if (filteredAppointments.isEmpty()) View.VISIBLE else View.GONE
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    appointments.add(PatientAppointment(appointment, null))
                    completed++
                    if (completed == total) {
                        filteredAppointments.clear()
                        filteredAppointments.addAll(appointments)
                        adapter.notifyDataSetChanged()
                        placeholderNoResults.visibility = if (filteredAppointments.isEmpty()) View.VISIBLE else View.GONE
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