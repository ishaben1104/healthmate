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

class DrAdapter(private val drs: List<tblDoctor>) :
    RecyclerView.Adapter<DrAdapter.DrViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_doctor, parent, false)
        return DrViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrViewHolder, position: Int) {
        val dr = drs[position]
        holder.bind(dr)
    }

    override fun getItemCount(): Int {
        return drs.size
    }

    class DrViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textViewDrName: TextView = itemView.findViewById(R.id.textViewDrName)
        private val textViewDesignation: TextView = itemView.findViewById(R.id.textViewDesignation)
        private val imageViewDr: ImageView = itemView.findViewById(R.id.imageViewDr)

        fun bind(dr: tblDoctor) {
            textViewDrName.text = "${dr.firstname} ${dr.lastname}"
            textViewDesignation.text = dr.designation

            // Load doctors image using Glide library
            val requestOptions = RequestOptions.circleCropTransform()
            val placeholderDrawable: Drawable? = ContextCompat.getDrawable(itemView.context, R.drawable.ic_doctor1)

            Glide.with(itemView)
                .load(dr.imageUrl)
                .apply(requestOptions)
                .placeholder(placeholderDrawable)
                .error(placeholderDrawable)
                .into(imageViewDr)
        }
    }
}