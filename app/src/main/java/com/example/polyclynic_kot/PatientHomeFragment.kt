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
    private var doctorsList = mutableListOf<DoctorResponse>()
    private lateinit var adapter: ListDocAdapter

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

        adapter = ListDocAdapter(doctorsList, isSpecializationMode = false) { doctor ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.content_frame_pat, DoctorSpecListFragment.newInstance(doctor.specialization))
                .addToBackStack(null)
                .commit()
        }
        recyclerView.adapter = adapter
        loadDoctors()
    }

    private fun loadDoctors() {
        ApiClientBase.authApi.getDoctors().enqueue(object : Callback<List<DoctorResponse>> {
            override fun onResponse(
                call: Call<List<DoctorResponse>?>,
                response: Response<List<DoctorResponse>?>
            ) {
                if (!isAdded) return

                response.body()?.let { doctors ->
                    doctorsList.clear()
                    doctorsList.addAll(doctors)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<DoctorResponse>?>, t: Throwable) {
                Toast.makeText(context, "Ошибка загрузки: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}






/*
override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.doctor_list, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.docList)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ListDocAdapter(originalItems, object : ListDocAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.content_frame_pat, DoctorSpecListFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }

        })
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.etSearchDoc)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null ) {
                    filterList(newText)
                }
                return false
            }

        })

        return view
    }

    private fun filterList(text: String) {
        val filteredList = originalItems.filter {
            it.contains(text, ignoreCase = true)
        }
        adapter.filterList(filteredList)
    }
 */