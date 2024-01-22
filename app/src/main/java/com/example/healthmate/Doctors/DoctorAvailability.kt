package com.example.healthmate.Doctors

data class DoctorAvailability(
    val doctorID: String,
    val availability: Map<String, Availability>
)