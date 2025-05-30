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

class PatientHomeFragment : Fragment() {
    private var doctorsList = listOf<DoctorResponse>()
    private lateinit var adapter: ListDocAdapter
    private var filteredSpecializationList = mutableListOf<DoctorResponse>()

    private val SEARCH_PREF = "search_history_doc"
    private val SEARCH_KEY = "history_list"
    private val MAX_HISTORY_SIZE = 10
    private var searchHistory = mutableListOf<String>()

    private lateinit var historyAdapter: android.widget.ArrayAdapter<String>
    private lateinit var searchHistoryList: android.widget.ListView
    private lateinit var clearHistoryButton: View

    private lateinit var placeholderEmpty: View
    private lateinit var placeholderError: View
    private lateinit var retryButton: View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.doctor_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.docList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ListDocAdapter(filteredSpecializationList, isSpecializationMode = false) { doctor ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame_pat, DoctorSpecListFragment.newInstance(doctor.specialization))
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter

        searchHistoryList = view.findViewById(R.id.search_his_list)
        clearHistoryButton = view.findViewById(R.id.clear_his_button)

        placeholderEmpty = view.findViewById(R.id.placeholder_empty)
        placeholderError = view.findViewById(R.id.placeholder_error)
        retryButton = view.findViewById(R.id.button_retry)

        retryButton.setOnClickListener {
            loadDoctors()
            placeholderError.visibility = View.GONE
        }

        loadSearchHistory()
        setupHistoryUI()

        setupSearch(view)
        setupSearchButton(view)
        loadDoctors()
    }

    private fun setupSearch(view: View) {
        val searchView = view.findViewById<SearchView>(R.id.etSearchDoc)

        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            if (hasFocus) showSearchHistory()
            else hideSearchHistory()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    saveSearchQuery(it)
                    filterSpecializations(it)
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
            val searchView = view?.findViewById<SearchView>(R.id.etSearchDoc)
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
        val searchView = view.findViewById<SearchView>(R.id.etSearchDoc)
        val button = view.findViewById<View>(R.id.bSearchDoc)

        button.setOnClickListener {
            val query = searchView.query.toString().trim()
            if (query.isNotEmpty()) {
                saveSearchQuery(query)
                filterSpecializations(query)
                hideSearchHistory()
            }
        }
    }

    private fun filterSpecializations(query: String) {
        val lowerQuery = query.lowercase()
        filteredSpecializationList.clear()

        val results = doctorsList.filter {
            it.specialization.lowercase().contains(lowerQuery)
        }

        if (results.isEmpty()) {
            placeholderEmpty.visibility = View.VISIBLE
        } else {
            placeholderEmpty.visibility = View.GONE
            filteredSpecializationList.addAll(results)
        }

        adapter.notifyDataSetChanged()
    }

    private fun loadDoctors() {
        placeholderEmpty.visibility = View.GONE
        placeholderError.visibility = View.GONE

        ApiClientBase.authApi.getDoctors().enqueue(object : Callback<List<DoctorResponse>> {
            override fun onResponse(
                call: Call<List<DoctorResponse>?>,
                response: Response<List<DoctorResponse>?>
            ) {
                if (!isAdded) return

                if (response.isSuccessful && response.body() != null) {
                    val doctors = response.body()!!
                    val uniqueSpecs = doctors.map { it.specialization }.distinct()

                    doctorsList = uniqueSpecs.map { spec ->
                        DoctorResponse(
                            doctor_id = 0L,
                            emailDoc = "",
                            name = "",
                            specialization = spec,
                            license = "",
                            phone = ""
                        )
                    }

                    filteredSpecializationList.clear()
                    filteredSpecializationList.addAll(doctorsList)
                    adapter.notifyDataSetChanged()

                    placeholderEmpty.visibility = if (doctorsList.isEmpty()) View.VISIBLE else View.GONE
                } else {
                    showErrorPlaceholder()
                }
            }

            override fun onFailure(call: Call<List<DoctorResponse>?>, t: Throwable) {
                if (!isAdded) return
                showErrorPlaceholder()
            }
        })
    }

    private fun showErrorPlaceholder() {
        placeholderError.visibility = View.VISIBLE
        placeholderEmpty.visibility = View.GONE
        filteredSpecializationList.clear()
        adapter.notifyDataSetChanged()
    }
}