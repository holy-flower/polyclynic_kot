package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class DoctorTechniquesFragment : Fragment() {
    private var originalItems = listOf("item 1", "item 2")
    private lateinit var adapter: ListPatAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_doctor_techniques, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.patListMed)
        recyclerView.layoutManager = LinearLayoutManager(context)

        adapter = ListPatAdapter(originalItems, object : ListPatAdapter.OnItemClickListener {
            override fun onItemClick(position: Int) {
                when (position) {
                    0 -> {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.content_frame_doc, AddMedicalHistoryPatFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        })
        recyclerView.adapter = adapter

        val searchView = view.findViewById<SearchView>(R.id.etSearchPatTech)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
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
}