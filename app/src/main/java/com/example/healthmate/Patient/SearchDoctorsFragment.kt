package com.example.healthmate.Patient

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthmate.R
import com.example.healthmate.tblDoctor
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class SearchDoctorsFragment : Fragment() {
    private lateinit var doctorsRecyclerView: RecyclerView
    private lateinit var doctorListAdapter: DoctorListAdapter
    val dateFormat = "yyyy-MM-dd"

    private val db = FirebaseFirestore.getInstance()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search_doctors, container, false)

        // Retrieve arguments
        val selectedDate = arguments?.getString("selectedDate")
        val selectedTime = arguments?.getString("selectedTime")
        val message = arguments?.getString("message")

        // Initialize RecyclerView and DoctorListAdapter
        doctorsRecyclerView = view.findViewById(R.id.doctorsRecyclerView)
        doctorListAdapter = DoctorListAdapter(emptyList())
        doctorsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        doctorsRecyclerView.adapter = doctorListAdapter

        // Fetch and update doctors on the selected day
        //fetchDoctorsOnSelectedDay(selectedDate.toString())  // Replace with the actual selected date

        return view
    }

//    private fun fetchDoctorsOnSelectedDay(selectedAppointmentDate: String) {
//        val dayOfWeek = getDayOfWeek(selectedAppointmentDate)
//
//        fetchAvailabilityDetails(dayOfWeek) { availableDoctorIds ->
//            if (availableDoctorIds.isNotEmpty()) {
//                fetchDoctorsDetails(availableDoctorIds) { doctorsList ->
//                    // Update the adapter with the list of doctors
//                    doctorListAdapter.updateDoctors(doctorsList)
//                }
//            } else {
//                // Handle the case where no doctors are available on the selected day
//            }
//        }
//    }

    fun getDayOfWeekFromDate(dateString: String, dateFormat: String): String {
        val formatter = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter.ofPattern(dateFormat)
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val date = LocalDate.parse(dateString, formatter)
        val dayOfWeek = date.dayOfWeek.toString()

        return dayOfWeek
    }

    private fun fetchAvailabilityDetails(dayOfWeek: String, callback: (List<String>) -> Unit) {
        // Implement logic to fetch availability details for the selected day
        // ...
    }

    private fun fetchDoctorsDetails(doctorIds: List<String>, callback: (List<tblDoctor>) -> Unit) {
        // Implement logic to fetch additional details for the given list of doctor IDs
        // ...
    }

}