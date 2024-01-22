package com.example.healthmate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.*

class AppointmentsViewModel {
    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _appointments = MutableLiveData<List<Appointment>>()
    val appointments: LiveData<List<Appointment>> get() = _appointments

    init {
        // Attach a listener to retrieve appointments when data changes
        databaseReference.child("appointments").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val appointmentsList = mutableListOf<Appointment>()

                for (appointmentSnapshot in snapshot.children) {
                    val appointment = appointmentSnapshot.getValue(Appointment::class.java)
                    appointment?.let {
                        val patientId = it.patientId.toString()

                        // Retrieve patient information using patientId
                        databaseReference.child("patients").child(patientId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(patientSnapshot: DataSnapshot) {
                                val patientFirstName = patientSnapshot.child("firstname").getValue(String::class.java)
                                val patientLastName = patientSnapshot.child("lastname").getValue(String::class.java)

                                // Update patientName in the appointment
                                it.patientName = "$patientFirstName $patientLastName"
                                appointmentsList.add(it)
                                _appointments.value = appointmentsList
                            }

                            override fun onCancelled(error: DatabaseError) {
                                // Handle errors
                            }
                        })
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle errors
            }
        })
    }
}