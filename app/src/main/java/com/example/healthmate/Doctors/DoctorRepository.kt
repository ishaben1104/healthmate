package com.example.healthmate.Doctors

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class DoctorRepository(private val firebaseManager: FirebaseManager) {

    private val _doctorDetails = MutableLiveData<DoctorDetails>()
    val doctorDetails: LiveData<DoctorDetails>
        get() = _doctorDetails

    private val _doctorAvailability = MutableLiveData<List<DoctorAvailability>>()
    val doctorAvailability: LiveData<List<DoctorAvailability>>
        get() = _doctorAvailability

    fun fetchDoctorDetails(doctorID: String) {
        firebaseManager.fetchDoctorDetails(doctorID) { details ->
            _doctorDetails.postValue(details)
        }
    }

    fun fetchDoctorAvailability() {
        firebaseManager.fetchDoctorAvailability { availabilityList ->
            _doctorAvailability.postValue(availabilityList)
        }
    }
}