package com.example.polyclynic_kot

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.polyclynic_kot.server.UserResponse

class AddMedicalHistoryPatFragment : Fragment() {
    private lateinit var user: UserResponse

    companion object {
        fun newInstance(user: UserResponse): AddMedicalHistoryPatFragment {
            return AddMedicalHistoryPatFragment().apply {
                arguments = Bundle().apply {
                    putParcelable("USER_DATA", user)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        user = arguments?.getParcelable("USER_DATA")!!
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<TextView>(R.id.tvNamePat).text = user.username
        view.findViewById<TextView>(R.id.tvBirthdayPat).text = user.datetime
        view.findViewById<TextView>(R.id.tvPolicyPat).text = user.policy
        view.findViewById<TextView>(R.id.tvPassportPat).text = user.passport
        view.findViewById<TextView>(R.id.tvRegistrationPat).text = user.registerPlace
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.patient_add_medical_history, container, false)
    }
}