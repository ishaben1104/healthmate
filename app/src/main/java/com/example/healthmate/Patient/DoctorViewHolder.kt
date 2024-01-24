package com.example.healthmate.Patient

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R
import com.example.healthmate.tblDoctor

// DoctorViewHolder.kt
class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val doctorImageView: ImageView = itemView.findViewById(R.id.doctorImageView)
    private val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
    private val doctorDesignationTextView: TextView = itemView.findViewById(R.id.doctorDesignationTextView)
    private val bookButton: Button = itemView.findViewById(R.id.bookButton)

    fun bind(doctor: tblDoctor) {
        // Bind doctor data to views
        val fullname = doctor.firstname + " " + doctor.lastname
        doctorNameTextView.text = fullname
        doctorDesignationTextView.text = doctor.designation

        // Load image using Glide or set a default image
        // Glide.with(itemView.context).load(doctor.imageUrl).into(doctorImageView)
        // For simplicity, using a placeholder image
        doctorImageView.setImageResource(R.drawable.doctor_avatar)

        // Handle button click or other interactions
        bookButton.setOnClickListener {
            // Handle book button click
        }
    }
}
