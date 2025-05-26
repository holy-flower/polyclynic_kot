package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.DoctorResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorSpecListFragment : Fragment() {
    private var doctorList = mutableListOf<DoctorResponse>()
    private var filteredDoctorList = mutableListOf<DoctorResponse>()

    private lateinit var adapter: ListDocAdapter
    private var specialization: String? = null

    private val SEARCH_PREF = "search_history_doc_spec"
    private val SEARCH_KEY = "history_list"
    private val MAX_HISTORY_SIZE = 10

    private lateinit var searchHistoryList: android.widget.ListView
    private lateinit var clearHistoryButton: View
    private lateinit var historyAdapter: android.widget.ArrayAdapter<String>
    private var searchHistory = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        specialization = arguments?.getString("SPECIALIZATION_KEY")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_doctor_spec_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.docListSpec)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ListDocAdapter(filteredDoctorList, isSpecializationMode = true) { doctor ->
            showDoctorDetails(doctor)
        }

        recyclerView.adapter = adapter

        searchHistoryList = view.findViewById(R.id.search_history_list_spec)
        clearHistoryButton = view.findViewById(R.id.clear_history_button_spec)

        loadSearchHistory()
        setupHistoryUI()

        setupSearch(view)
        setupSearchButton(view)
        loadDoctorsBySpecialization()
    }

    private fun setupSearch(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchDocSpec)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) showSearchHistory()
            else hideSearchHistory()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    saveSearchQuery(it)
                    filterDoctors(it)
                    hideSearchHistory()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun loadSearchHistory() {
        val prefs = requireContext().getSharedPreferences(SEARCH_PREF, android.content.Context.MODE_PRIVATE)
        searchHistory.clear()

        val savedString = prefs.getString(SEARCH_KEY, null)
        if (!savedString.isNullOrEmpty()) {
            searchHistory.addAll(savedString.split(",").filter { it.isNotBlank() })
        }
    }

    private fun saveSearchQuery(query: String) {
        if (query.isBlank()) return

        searchHistory.remove(query)
        searchHistory.add(0, query)

        while (searchHistory.size > MAX_HISTORY_SIZE) {
            searchHistory.removeAt(searchHistory.lastIndex)
        }

        val prefs = requireContext().getSharedPreferences(SEARCH_PREF, android.content.Context.MODE_PRIVATE)
        prefs.edit().putString(SEARCH_KEY, searchHistory.joinToString(",")).apply()

        if (::historyAdapter.isInitialized) {
            historyAdapter.notifyDataSetChanged()
        }
    }

    private fun clearSearchHistory() {
        searchHistory.clear()
        val prefs = requireContext().getSharedPreferences(SEARCH_PREF, android.content.Context.MODE_PRIVATE)
        prefs.edit().remove(SEARCH_KEY).apply()

        if (::historyAdapter.isInitialized) {
            historyAdapter.notifyDataSetChanged()
        }

        hideSearchHistory()
    }

    private fun setupHistoryUI() {
        historyAdapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, searchHistory)
        searchHistoryList.adapter = historyAdapter

        searchHistoryList.setOnItemClickListener { _, _, position, _ ->
            val selected = searchHistory[position]
            val searchView = view?.findViewById<SearchView>(R.id.etSearchDocSpec)
            searchView?.setQuery(selected, true)
            hideSearchHistory()
        }

        clearHistoryButton.setOnClickListener {
            clearSearchHistory()
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

    private fun setupSearchButton(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchDocSpec)
        val button = view.findViewById<View>(R.id.bSearchDocSpec)

        button.setOnClickListener {
            val query = searchView.query.toString().trim()
            if (query.isNotEmpty()) {
                saveSearchQuery(query)
                filterDoctors(query)
                hideSearchHistory()
            }
        }
    }

    private fun filterDoctors(query: String) {
        val lowerQuery = query.lowercase()

        filteredDoctorList.clear()
        filteredDoctorList.addAll(
            doctorList.filter {
                it.name.lowercase().contains(lowerQuery)
            }
        )
        adapter.notifyDataSetChanged()

        if (filteredDoctorList.isEmpty()) {
            Toast.makeText(requireContext(), "Ничего не найдено", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDoctorDetails (doctor: DoctorResponse) {
        val args = Bundle().apply {
            putLong("DOCTOR_ID", doctor.doctor_id)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame_pat, AppointmentFragment.newInstance(doctor.doctor_id))
            .addToBackStack(null)
            .commit()
    }

    private fun loadDoctorsBySpecialization() {
        val spec = specialization ?: return
        ApiClientBase.authApi.getDoctorsBySpecialization(spec).enqueue(object : Callback<List<DoctorResponse>> {
            override fun onResponse(
                call: Call<List<DoctorResponse>?>,
                response: Response<List<DoctorResponse>?>
            ) {
                println("Server response: ${response.body()}")

                if (response.isSuccessful) {
                    response.body()?.let { doctors ->
                        doctorList.clear()
                        doctorList.addAll(doctors)

                        filteredDoctorList.clear()
                        filteredDoctorList.addAll(doctorList)

                        adapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(requireContext(), "Ошибка сервера", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<DoctorResponse>?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    companion object {
        fun newInstance(specialization: String): DoctorSpecListFragment {
            return DoctorSpecListFragment().apply {
                arguments = Bundle().apply {
                    putString("SPECIALIZATION_KEY", specialization)
                }
            }
        }
    }
}