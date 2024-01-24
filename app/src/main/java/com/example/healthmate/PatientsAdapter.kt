package com.example.healthmate


import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class PatientsAdapter(private val patients: List<tblPatient>) :
    RecyclerView.Adapter<PatientsAdapter.PatientViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_patient, parent, false)
        return PatientViewHolder(view)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = patients[position]
        holder.bind(patient)
    }

    override fun getItemCount(): Int {
        return patients.size
    }

    class PatientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewPatientName: TextView = itemView.findViewById(R.id.textViewPatientName)
        private val textViewDOB: TextView = itemView.findViewById(R.id.textViewDOB)
        private val imageViewPatient: ImageView = itemView.findViewById(R.id.imageViewPatient)

        fun bind(patient: tblPatient) {
            textViewPatientName.text = patient.fullName
            textViewDOB.text = patient.dob

            // Load patient image using Glide library
            val requestOptions = RequestOptions.circleCropTransform()
            val placeholderDrawable: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.patient_avatar)

            Glide.with(itemView)
                .load(patient.imageUrl)
                .apply(requestOptions)
                .placeholder(placeholderDrawable)
                .error(placeholderDrawable)
                .into(imageViewPatient)
        }
    }
}