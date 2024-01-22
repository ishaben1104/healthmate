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

class fragment_patients : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PatientsAdapter
    private val patientList = mutableListOf<tblPatient>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_patients, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewPatients)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Initialize Firebase
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("patients") // Replace with your Firebase node

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Clear the previous data
                patientList.clear()

                for (patientSnapshot in dataSnapshot.children) {
                    val fName = patientSnapshot.child("firstname").getValue(String::class.java)
                    val lName = patientSnapshot.child("lastname").getValue(String::class.java)
                    val dob = patientSnapshot.child("dob").getValue(String::class.java)
                    var imageUrl = patientSnapshot.child("imageUrl").getValue(String::class.java)

                    if (imageUrl == null) {
                        imageUrl = "@drawable/ic_patient1"
                    }

                    if (fName != null && lName != null && dob != null && imageUrl != null) {
                        val patient = tblPatient("$fName $lName", dob.toString(), imageUrl.toString())
                        patientList.add(patient)
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
        adapter = PatientsAdapter(patientList)
        recyclerView.adapter = adapter

        return view
    }
}
