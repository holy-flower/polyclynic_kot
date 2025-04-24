package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class PatientHomeFragment : Fragment() {
    private var originalItems = listOf("item 1", "Item 2")
    private lateinit var adapter: ListDocAdapter

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
}