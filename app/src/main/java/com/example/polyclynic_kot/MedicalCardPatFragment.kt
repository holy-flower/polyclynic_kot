package com.example.polyclynic_kot

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicalCardPatFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,  container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_info, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userId = getCurrentUserId()
        if (userId == -1L) return

        ApiClientBase.authApi.getUserById(userId).enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!

                    view.findViewById<TextView>(R.id.tvNamePatient).text = "ФИО: ${user.username}"
                    view.findViewById<TextView>(R.id.tvBirthdayPatient).text = "Дата рождения: ${user.datetime}"
                    view.findViewById<TextView>(R.id.tvPolicyPatient).text = "Полис: ${user.policy}"
                    view.findViewById<TextView>(R.id.tvPassportPatient).text = "Паспортные данные: ${user.passport}"
                    view.findViewById<TextView>(R.id.tvRegistrationPatient).text = "Место регистрации: ${user.registerPlace}"
                }
            }

            override fun onFailure(call: Call<UserResponse?>, t: Throwable) {
                Toast.makeText(requireContext(), "Ошибка загрузки данных", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getCurrentUserId(): Long {
        val prefs = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return prefs.getLong("USER_ID", -1L)
    }
}