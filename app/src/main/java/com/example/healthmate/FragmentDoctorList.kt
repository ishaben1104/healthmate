package com.example.healthmate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FragmentDoctorList : Fragment() {

private lateinit var recyclerView: RecyclerView
private lateinit var adapter: DrAdapter
private val drList = mutableListOf<tblDoctor>()

override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
): View? {
    val view = inflater.inflate(R.layout.fragment_doctor_list, container, false)

    recyclerView = view.findViewById(R.id.recyclerViewDrs)
    recyclerView.layoutManager = LinearLayoutManager(requireContext())

    // Initialize Firebase
    val database = FirebaseDatabase.getInstance()
    val reference = database.getReference("doctors") // Replace with your Firebase node

    reference.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Clear the previous data
            drList.clear()

            for (drSnapshot in dataSnapshot.children) {
                val fName = drSnapshot.child("firstname").getValue(String::class.java)
                val lName = drSnapshot.child("lastname").getValue(String::class.java)
                val designation = drSnapshot.child("designation").getValue(String::class.java)
                var imageUrl = drSnapshot.child("imageUrl").getValue(String::class.java)

                if (imageUrl == null) {
                    imageUrl = "@drawable/ic_doctor1"
                }

                if (fName != null && lName != null && designation != null && imageUrl != null) {
                    val dr = tblDoctor(fName, lName, designation, imageUrl.toString())
                    drList.add(dr)
                }
            }
            // Update the adapter with the retrieved patient list
            adapter.notifyDataSetChanged()
        }

        override fun onCancelled(databaseError: DatabaseError) {
            // Handle database error (optional)
        }
    })

    // Initialize the adapter with an empty list
    adapter = DrAdapter(drList)
    recyclerView.adapter = adapter

    return view
}
}
