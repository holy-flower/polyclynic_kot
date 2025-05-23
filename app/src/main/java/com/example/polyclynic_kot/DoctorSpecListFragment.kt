package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.DoctorResponse
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DoctorSpecListFragment : Fragment() {
    private var doctorList = mutableListOf<DoctorResponse>()
    private lateinit var adapter: ListDocAdapter
    private var specialization: String? = null

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

        adapter = ListDocAdapter(doctorList, isSpecializationMode = true) { doctor ->
            showDoctorDetails(doctor)
        }

        recyclerView.adapter = adapter
        loadDoctorsBySpecialization()
    }

    private fun showDoctorDetails (doctor: DoctorResponse) {
        val args = Bundle().apply {
            putLong("DOCTOR_ID", doctor.doctor_id)
        }

        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame_pat, AppointmentFragment.newInstance(doctor.doctor_id))
            .addToBackStack(null)
            .commit()

        /*
        val doctorInfoFragment = DoctorInfoFragment.newInstance(doctor)

        parentFragmentManager.beginTransaction()
            .replace(R.id.content_frame_pat, doctorInfoFragment)
            .addToBackStack(null)
            .commit()
         */
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

    //private var loadJob: Job? = null
    //override fun onDestroyView() {
        //loadJob?.cancel()
        //super.onDestroyView()
    //}

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