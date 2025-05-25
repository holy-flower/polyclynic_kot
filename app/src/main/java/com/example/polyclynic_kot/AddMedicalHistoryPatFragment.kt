package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.polyclynic_kot.server.ApiClientBase
import com.example.polyclynic_kot.server.UserResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddMedicalHistoryPatFragment : Fragment() {
    private lateinit var user: UserResponse
    private var doctorId: Long = -1
    private lateinit var date: String
    private lateinit var time: String

    companion object {
        fun newInstance(user: UserResponse, doctorId: Long, date: String, time: String): AddMedicalHistoryPatFragment {
            return AddMedicalHistoryPatFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("USER_DATA", user)
                    putLong("DOCTOR_ID", doctorId)
                    putString("DATE", date)
                    putString("TIME", time)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            user = it.getParcelable("USER_DATA")!!
            doctorId = it.getLong("DOCTOR_ID")
            date = it.getString("DATE")!!
            time = it.getString("TIME")!!
        }
        //user = arguments?.getParcelable("USER_DATA")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvNamePat).text = "ФИО: ${user.username}"
        view.findViewById<TextView>(R.id.tvBirthdayPat).text = "Дата рождения: ${user.datetime}"
        view.findViewById<TextView>(R.id.tvPolicyPat).text = "Полис: ${user.policy}"
        view.findViewById<TextView>(R.id.tvPassportPat).text = "Паспорт: ${user.passport}"
        view.findViewById<TextView>(R.id.tvRegistrationPat).text = "Место регистрации: ${user.registerPlace}"

        val etMedicalHistory = view.findViewById<EditText>(R.id.etMedicalHistory)
        val bSave = view.findViewById<Button>(R.id.bSave)

        bSave.setOnClickListener {
            val noteText = etMedicalHistory.text.toString()

            if (noteText.isEmpty()) {
                Toast.makeText(requireContext(), "Введите текст истории болезни", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            ApiClientBase.authApi.updateMedicalNotes(
                doctorId = doctorId,
                userId = user.user_id,
                date = date,
                time = time,
                notes = noteText
            ).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "История болезни сохранена", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(requireContext(), "Ошибка сервера: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Ошибка: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_add_medical_history, container, false)
    }
}