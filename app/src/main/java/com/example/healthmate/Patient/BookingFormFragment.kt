package com.example.healthmate.Patient

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.healthmate.R
import com.example.healthmate.fragment_home
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale


class BookingFormFragment : Fragment() {
    private lateinit var dateEditText: TextInputEditText
    private lateinit var timeEditText: TextInputEditText
    private lateinit var messageEditText: TextInputEditText
    private lateinit var databaseReference: DatabaseReference
    private lateinit var calendar: Calendar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_booking_form, container, false)

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("appointments")
        dateEditText = view.findViewById(R.id.dateEditText)
        timeEditText = view.findViewById(R.id.timeEditText)
        messageEditText = view.findViewById(R.id.messageEditText)
        calendar = Calendar.getInstance()

        timeEditText.setOnClickListener {
            showTimePickerDialog()
        }

        dateEditText.setOnClickListener {
            showDatePickerDialog()
        }

        view.findViewById<View>(R.id.nextButton).setOnClickListener {
            val date = dateEditText.text.toString().trim()
            val time = timeEditText.text.toString().trim()
            val message = messageEditText.text.toString().trim()

            // Validate data
            var hasError = false

            if (date.isEmpty()) {
                dateEditText.error = "Please select appointment date"
                hasError = true
            } else {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                try {
                    val dateOfBirth = sdf.parse(date)
                    val currentDate = Date()

                    if (dateOfBirth != null && dateOfBirth.before(currentDate)) {
                        dateEditText.error = "Appointment date should be a future date"
                        hasError = true
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    dateEditText.error = "Invalid date format. Please use dd/MM/yyyy"
                    hasError = true
                }
            }

            if (time.isEmpty()) {
                timeEditText.error = "Please enter appointment time"
                hasError = true
            }

            if (message.isEmpty()) {
                messageEditText.error = "Please enter your concerns"
                hasError = true
            }

            if (!hasError) {
                // Generate a unique appointmentId based on date and time
                val appointmentId = databaseReference.push().key

                // Get doctorId and patientId from your session or wherever you have them
                val sharedPreferences = requireContext().getSharedPreferences("MySession", Context.MODE_PRIVATE)

                val doctorId = ""
                val patientId = sharedPreferences.getString("loginid", "").toString()

                // Save data to Firebase Database
                val appointmentData = AppointmentData(
                    appointmentId = appointmentId.toString(),
                    appDate = date,
                    appTime = time,
                    concerns = message,
                    doctorId = doctorId,
                    patientId = patientId,
                    status = "Pending"
                )

                // Check for duplicate appointments
                checkDuplicateAppointments(appointmentData, view)
            } else {
                Snackbar.make(view, "Fill the required fields", Snackbar.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun showTimePickerDialog() {
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            R.style.TimePickerDialogTheme,
            TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                updateTimeEditText()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            false
        )
        timePickerDialog.show()
    }

    private fun updateTimeEditText() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        timeEditText.setText(timeFormat.format(calendar.time))
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            R.style.DatePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateEditText()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun updateDateEditText() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        dateEditText.setText(dateFormat.format(calendar.time))
    }

    private fun checkDuplicateAppointments(appointmentData: AppointmentData, view: View) {
        val query = databaseReference.orderByChild("appDate").equalTo(appointmentData.appDate)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isDuplicate = false

                for (snapshot in dataSnapshot.children) {
                    val existingAppointment = snapshot.getValue(AppointmentData::class.java)
                    if (existingAppointment != null &&
                        existingAppointment.appTime == appointmentData.appTime &&
                        existingAppointment.status == "Scheduled"
                    ) {
                        isDuplicate = true
                        break
                    }
                }

                if (!isDuplicate) {
                    // Save data to Firebase Database
                    databaseReference.child(appointmentData.appointmentId).setValue(appointmentData)

                    // Success message using Snackbar
                    Snackbar.make(view, "Appointment booked successfully", Snackbar.LENGTH_SHORT).show()

                    // Clear input fields if needed
                    dateEditText.text?.clear()
                    timeEditText.text?.clear()
                    messageEditText.text?.clear()

                    // Navigate to the home fragment after successful update
                    val fragmentManager = requireActivity().supportFragmentManager
                    fragmentManager.beginTransaction()
                        .replace(R.id.patient_fragment_container, fragment_home()) // Replace with your HomeFragment class
                        .addToBackStack(null) // Optional: Add to back stack
                        .commit()
                } else {
                    // Duplicate appointment found
                    Snackbar.make(view, "Duplicate appointment. Please choose a different date/time", Snackbar.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                Snackbar.make(view, "Error checking appointment", Snackbar.LENGTH_SHORT).show()
            }
        })
    }
}
