package com.example.healthmate

import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.FirebaseDatabase
import java.util.Locale

class fragment_dr_availability : Fragment() {

    private val database = FirebaseDatabase.getInstance()
    private val availabilityReference = database.getReference("doctor_availability")
    private lateinit var buttonSaveAvailability: Button
    private val daysOfWeek = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_dr_availability, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonSaveAvailability = view.findViewById(R.id.buttonSaveAvailability)

        buttonSaveAvailability.setOnClickListener {
            val doctorId = getDoctorId()

            if (!doctorId.isNullOrBlank()) {
                var shouldRedirect = true
                for (day in daysOfWeek) {
                    val checkBoxId = resources.getIdentifier("checkBox$day", "id", requireContext().packageName)
                    val timeInId = resources.getIdentifier("editText${day}In", "id", requireContext().packageName)
                    val timeOutId = resources.getIdentifier("editText${day}Out", "id", requireContext().packageName)

                    val checkBox = view.findViewById<CheckBox>(checkBoxId)
                    val timeIn = view.findViewById<EditText>(timeInId)
                    val timeOut = view.findViewById<EditText>(timeOutId)

                    if (!(checkBox.isChecked && timeIn.text.isNotBlank() && timeOut.text.isNotBlank()) &&
                        (checkBox.isChecked || timeIn.text.isNotBlank() || timeOut.text.isNotBlank())
                    ) {
                        // If either checkbox or time fields are partially filled, show an error
                        shouldRedirect = false
                        Toast.makeText(
                            requireContext(),
                            "Please leave all checkboxes unchecked and all time fields empty or fill all 3 of them for each day to save.",
                            Toast.LENGTH_SHORT
                        ).show()
                        break
                    }
                }

                if (shouldRedirect) {
                    saveAvailability(doctorId)
                    // Redirect or handle success as needed
                    showToast("Availability set successfully")
                    val intent = Intent(requireContext(), DrHomeActivity::class.java)
                    startActivity(intent)
                }

            } else {
                Toast.makeText(requireContext(), "Error: Doctor ID not found.", Toast.LENGTH_SHORT).show()
            }
        }

        // Set up time picker for each day
        for (day in daysOfWeek) {
            val timeInId = resources.getIdentifier("editText${day}In", "id", requireContext().packageName)
            val timeOutId = resources.getIdentifier("editText${day}Out", "id", requireContext().packageName)

            val timeInEditText = view.findViewById<TextInputEditText>(timeInId)
            val timeOutEditText = view.findViewById<TextInputEditText>(timeOutId)

            timeInEditText.filters = arrayOf(FixedTimeFormatInputFilter())
            timeOutEditText.filters = arrayOf(FixedTimeFormatInputFilter())

            // Set OnClickListener for opening the TimePickerDialog
            setEditTextClickListener(timeInEditText)
            setEditTextClickListener(timeOutEditText)
        }
    }

    private fun setEditTextClickListener(editText: EditText) {
        editText.setOnClickListener { showTimePickerDialog(editText) }

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                showTimePickerDialog(editText)
            }
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            requireContext(),
            TimePickerDialog.OnTimeSetListener { _, selectedHour, selectedMinute ->
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                editText.setText(formattedTime)
            },
            hour,
            minute,
            false
        )
        timePickerDialog.show()
    }

    private fun getDoctorId(): String? {
        val sharedPreferences = requireContext().getSharedPreferences("MySession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("loginid", "")
    }

    private fun saveAvailability(doctorId: String) {
        val doctorAvailabilityReference = availabilityReference.child(doctorId)
        for (day in daysOfWeek) {
            val checkBoxId = resources.getIdentifier("checkBox$day", "id", requireContext().packageName)
            val timeInId = resources.getIdentifier("editText${day}In", "id", requireContext().packageName)
            val timeOutId = resources.getIdentifier("editText${day}Out", "id", requireContext().packageName)

            val checkBox = view?.findViewById<CheckBox>(checkBoxId)
            val timeIn = view?.findViewById<EditText>(timeInId)?.text.toString()
            val timeOut = view?.findViewById<EditText>(timeOutId)?.text.toString()

            val dayReference = doctorAvailabilityReference.child(day)
            dayReference.child("isAvailable").setValue(checkBox?.isChecked)
            dayReference.child("timeIn").setValue(timeIn)
            dayReference.child("timeOut").setValue(timeOut)
        }

    }

    fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}
