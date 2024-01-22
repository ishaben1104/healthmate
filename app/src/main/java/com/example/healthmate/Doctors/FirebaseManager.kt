package com.example.healthmate.Doctors

import com.google.firebase.database.*

class FirebaseManager {

    fun fetchDoctorDetails(doctorID: String, callback: (DoctorDetails?) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val doctorsRef: DatabaseReference = database.getReference("doctors").child(doctorID)

        doctorsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctorDetails = dataSnapshot.getValue(DoctorDetails::class.java)
                callback(doctorDetails)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                callback(null)
            }
        })
    }

    fun fetchDoctorAvailability(callback: (List<DoctorAvailability>) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val availabilityRef: DatabaseReference = database.getReference("doctor_availability")

        availabilityRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val doctorAvailabilityList = mutableListOf<DoctorAvailability>()

                for (doctorSnapshot in dataSnapshot.children) {
                    val doctorID = doctorSnapshot.key
                    val availabilityMap = doctorSnapshot.getValue(object : GenericTypeIndicator<Map<String, Availability>>() {})
                    val availability = availabilityMap?.mapKeys { it.key.toLowerCase() } ?: emptyMap()

                    doctorAvailabilityList.add(DoctorAvailability(doctorID!!, availability))
                }

                callback(doctorAvailabilityList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                callback(emptyList())
            }
        })
    }

}