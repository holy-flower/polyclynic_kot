package com.example.polyclynic_kot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.polyclynic_kot.server.DoctorResponse

class ListDocAdapter (private var doctors: List<DoctorResponse>,
                      private val isSpecializationMode: Boolean,
                      private val onItemClick: (DoctorResponse) -> Unit) : RecyclerView.Adapter<ListDocAdapter.DoctorViewHolder>() {

    inner class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val specialization: TextView = itemView.findViewById(R.id.tvSpecializationDoc)
        val name: TextView = itemView.findViewById(R.id.tvDoctorName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val layoutId = if (isSpecializationMode) R.layout.item_doc_list_spec else R.layout.item_doc_list

        val view = LayoutInflater.from(parent.context)
            .inflate(layoutId, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctors[position]
        if (isSpecializationMode) {
            holder.name.text = doctor.name
            holder.specialization.visibility = View.GONE
        } else {
            holder.specialization.text = doctor.specialization
            holder.name.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onItemClick?.invoke(doctor)
        }
    }

    override fun getItemCount() = doctors.size
}
