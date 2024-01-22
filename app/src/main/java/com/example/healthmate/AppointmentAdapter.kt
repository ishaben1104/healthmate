package com.example.healthmate

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class AppointmentAdapter(options: FirebaseRecyclerOptions<Appointment>) :
    FirebaseRecyclerAdapter<Appointment, AppointmentAdapter.AppointmentViewHolder>(options) {

    class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientNameTextView: TextView = itemView.findViewById(R.id.upd_patientNameTextView)
        val appDatetime: TextView = itemView.findViewById(R.id.upd_appDateTimeTextView)
        val doctorID: TextView = itemView.findViewById(R.id.upd_doctorIdTextView)
        val statusVal: TextView = itemView.findViewById(R.id.upd_statusTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appoinments_updated, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int, model: Appointment) {
        if (model.patientName.isNotEmpty()) {
            holder.patientNameTextView.text = model.patientName
        } else {
            holder.patientNameTextView.text = "John Trump"
        }
        //holder.patientNameTextView.text = model.patientName
        holder.appDatetime.text = "${model.appDate} ${model.appTime}"
        holder.doctorID.text = model.doctorName
        holder.statusVal.text = model.status
    }
}