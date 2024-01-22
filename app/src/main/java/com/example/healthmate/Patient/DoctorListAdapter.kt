package com.example.healthmate.Patient

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.example.healthmate.R
import com.example.healthmate.tblDoctor

class DoctorListAdapter(private var doctors: List<tblDoctor>) : RecyclerView.Adapter<DoctorListAdapter.DoctorViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_list_card, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val currentDoctor = doctors[position]

        holder.doctorNameTextView.text = currentDoctor.firstname + " " + currentDoctor.lastname
        holder.doctorDesignationTextView.text = currentDoctor.designation

        // Load image from URL or use a default image
//        if (currentDoctor.imageUrl.isNotEmpty()) {
//            Glide.with(holder.itemView.context)
//                .load(currentDoctor.imageUrl)
//                .apply(RequestOptions.circleCropTransform())
//                .transition(DrawableTransitionOptions.withCrossFade())
//                .into(holder.doctorImageView)
//        } else {
//            // Use a default image from the drawable resource
//            holder.doctorImageView.setImageResource(R.drawable.ic_doctor1)
//        }

        // Implement any additional bindings based on your layout
    }

    override fun getItemCount(): Int {
        return doctors.size
    }

    // Function to update the list of doctors and notify the adapter
    fun updateDoctors(newDoctors: List<tblDoctor>) {
        doctors = newDoctors
        notifyDataSetChanged()
    }

    // ViewHolder class
    class DoctorViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val doctorImageView: ImageView = itemView.findViewById(R.id.doctorImageView)
        val doctorNameTextView: TextView = itemView.findViewById(R.id.doctorNameTextView)
        val doctorDesignationTextView: TextView = itemView.findViewById(R.id.doctorDesignationTextView)

        // Add any additional views you want to bind here
    }
}