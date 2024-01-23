package com.example.healthmate

import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

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
        val databaseReference = FirebaseDatabase.getInstance().getReference("patients")
        val drRef = databaseReference.child(model.patientId)

        drRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val patientData = snapshot.getValue(Doctor::class.java)

                if (patientData != null) {
                    if (patientData.firstname?.isNotEmpty() == true) {
                        holder.patientNameTextView.text = "${patientData.firstname} ${patientData.lastname}"
                    } else {
                        holder.patientNameTextView.text = "John Trump"
                    }
                    //holder.patientNameTextView.text = model.patientName
                    holder.appDatetime.text = "${model.appDate} ${model.appTime}"
                    holder.doctorID.text = model.doctorName
                    holder.statusVal.text = model.status

                    Log.d("FirebaseData", "DrName: ${patientData.firstname}, Email: ${patientData.lastname}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseData", "Failed to read value.", error.toException())
            }
        })
    }
}