package com.example.healthmate

data class Doctor(
    val id: String? = null,
    var username: String? = null,
    var firstname: String? = null,
    var lastname: String? = null,
    var designation: String? = null,
    var email: String? = null,
    var phone: String? = null,
    val password: String? = null,
    var imageUrl: String? = null
)
