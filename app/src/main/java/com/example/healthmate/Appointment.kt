package com.example.healthmate

data class Appointment(
    val id: String = "",
    val patientId: String = "",
    var patientName: String = "",
    var doctorName: String = "",
    val appDate: String = "",
    val appTime: String = "",
    val doctorId: String = "",
    val status: String = "",
    val concerns: String = ""

)
