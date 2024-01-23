package com.example.healthmate

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AppointmentListAdapter(options: FirebaseRecyclerOptions<Appointment>) :
    FirebaseRecyclerAdapter<Appointment, AppointmentListAdapter.AppointmentViewHolder>(options) {

    private var onItemClickListener: OnItemClickListener? = null

    inner class AppointmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val patientNameTextView: TextView = itemView.findViewById(R.id.patientNameTextView)
        val appDatetime: TextView = itemView.findViewById(R.id.appDateTimeTextView)
        val Concerns: TextView = itemView.findViewById(R.id.txtConcerns)
        val approveButton: Button = itemView.findViewById(R.id.approveButton)
        val cancelButton: Button = itemView.findViewById(R.id.cancelButton)

        init {
            approveButton.setOnClickListener {
                onItemClickListener?.onApproveClick(adapterPosition, getItem(adapterPosition))
            }

            cancelButton.setOnClickListener {
                onItemClickListener?.onCancelClick(adapterPosition, getItem(adapterPosition))
            }
        }
    }

    interface OnItemClickListener {
        fun onApproveClick(position: Int, appointment: Appointment)
        fun onCancelClick(position: Int, appointment: Appointment)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_appointment, parent, false)

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
                    holder.appDatetime.text = "${model.appDate} ${model.appTime}"
                    holder.Concerns.text = model.concerns
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseData", "Failed to read value.", error.toException())
            }
        })

    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener
    }
}