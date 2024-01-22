package com.example.healthmate.Doctors

data class Availability(
    val isAvailable: Boolean,
    val timeIn: String,
    val timeOut: String
)