package com.example.polyclynic_kot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.polyclynic_kot.server.appointment.AppointmentResponse
import com.example.polyclynic_kot.server.appointment.PatientAppointment

class ListPatAdapter (private var items: MutableList<PatientAppointment>, private val onItemClick: (PatientAppointment) -> Unit) : RecyclerView.Adapter<ListPatAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamePat: TextView = view.findViewById(R.id.tvPatientName)
        val tvDateReg: TextView = view.findViewById(R.id.tvPatientDateReg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pat_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvNamePat.text = item.user?.username ?: "Неизвестный пациент"
        holder.tvDateReg.text = "${item.appointment.date} ${item.appointment.time}"
        holder.itemView.setOnClickListener { onItemClick(item) }
    }

    override fun getItemCount() = items.size

    fun updateList(newList: List<PatientAppointment>) {
        items.clear()
        items.addAll(newList)
        notifyDataSetChanged()
    }
}