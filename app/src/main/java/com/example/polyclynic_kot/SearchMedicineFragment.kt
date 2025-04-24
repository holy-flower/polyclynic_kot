/*
package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

class SearchMedicineFragment : Fragment() {

    private lateinit var searchView: SearchView
    private lateinit var tvNameResult: TextView
    private lateinit var tvCompositionResult: TextView
    private lateinit var tvUsageResult: TextView
    private lateinit var tvContraindicationsResult: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_medicine, container, false)

        searchView = view.findViewById(R.id.search_medicine)
        tvNameResult = view.findViewById(R.id.tvSearchResultName)
        tvCompositionResult = view.findViewById(R.id.tvSearchResultComposition)
        tvUsageResult = view.findViewById(R.id.tvSearchResultDosage)
        tvContraindicationsResult = view.findViewById(R.id.tvSearchResultContraindications)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    performSearch(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return true
            }
        })

        return view
    }

    private fun performSearch(query: String) {
        val call = ApiClient.apiService.getData(query)
        call.enqueue(object : Callback<YourResponseModel> {
            override fun onResponse(call: Call<YourResponseModel>, response: Response<YourResponseModel>) {
                if (response.isSuccessful) {
                    val data = response.body()

                    data?.let {
                        tvNameResult.text = it.name
                        tvCompositionResult.text = it.composition
                        tvUsageResult.text = it.usage
                        tvContraindicationsResult.text = it.contraindications
                    }
                }
            }

            override fun onFailure(call: Call<YourResponseModel>, t: Throwable) {
                println("Reques failed: ${t.message}")
            }

        })
    }
}
 */