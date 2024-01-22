package com.example.healthmate

import android.app.DatePickerDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.fragment.app.Fragment
import com.example.healthmate.Patient.PatientData
import com.example.healthmate.Patient.PtDataNoPwd
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EditPatientFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var patientRef: DatabaseReference
    private lateinit var editTextFirstname: AppCompatEditText
    private lateinit var editTextLastname: AppCompatEditText
    private lateinit var edittextPhone: AppCompatEditText
    private lateinit var edittextEmailAddress: AppCompatEditText
    private lateinit var edittextUsername: AppCompatEditText
    private lateinit var edittextDOB: AppCompatEditText
    private lateinit var edittextPassword: AppCompatEditText
    private lateinit var btnUpdatePtProfile: Button
    private val sharedPreferencesKey = "MySession"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_patient, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("patients")

        editTextFirstname = view.findViewById(R.id.editTextFirstname_pt)
        editTextLastname = view.findViewById(R.id.editTextLastname_pt)
        edittextPhone = view.findViewById(R.id.edittextPhone_pt)
        edittextEmailAddress = view.findViewById(R.id.edittextEmailAddress_pt)
        edittextUsername = view.findViewById(R.id.edittextUsername_pt)
        edittextDOB = view.findViewById(R.id.edittextDOB_pt)
        btnUpdatePtProfile = view.findViewById(R.id.btnUpdatePtProfile)

        edittextDOB.setOnClickListener {
            showDatePicker()
        }

        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userId: String = sharedPreferences.getString("loginid", null).toString()
        Log.d("Userids", "Name: ${userId}")
        patientRef = FirebaseDatabase.getInstance().getReference("patients").child(userId)

        // read data
        readDataFromFirebase(userId)

        btnUpdatePtProfile.setOnClickListener {
            // Update patient data on Firebase
            updatePatientData(userId)
        }

        return view
    }

    private fun readDataFromFirebase(patientId: String) {
        val patientRef = databaseReference.child(patientId)

        patientRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val patientData = snapshot.getValue(PatientData::class.java)

                if (patientData != null) {
                    editTextFirstname.text = Editable.Factory.getInstance().newEditable(patientData.firstname)
                    editTextLastname.text = Editable.Factory.getInstance().newEditable(patientData.lastname)
                    edittextPhone.text = Editable.Factory.getInstance().newEditable(patientData.phone)
                    edittextEmailAddress.text = Editable.Factory.getInstance().newEditable(patientData.email)
                    edittextUsername.text = Editable.Factory.getInstance().newEditable(patientData.username)
                    edittextDOB.text = Editable.Factory.getInstance().newEditable(patientData.dob)

                    Log.d("FirebaseData", "Name: ${patientData.firstname}, Email: ${patientData.lastname}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseData", "Failed to read value.", error.toException())
            }
        })
    }

    fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val edittextDOB = view?.findViewById<AppCompatEditText>(R.id.edittextDOB_pt)

        val datePickerDialog = DatePickerDialog(
            requireContext(),  // Use requireContext() to get the fragment's context
            R.style.DatePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                edittextDOB?.setText(sdf.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the maximum allowed date to the current date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

    private fun updatePatientData(patientId: String) {
        val firstName = editTextFirstname.text.toString()
        val lastName = editTextLastname.text.toString()
        val phone = edittextPhone.text.toString()
        val email = edittextEmailAddress.text.toString()
        val username = edittextUsername.text.toString()
        val dob = edittextDOB.text.toString()

        Log.w("FirebaseData Edit", "Firstname. $firstName, Lastname $lastName, Dob $dob")

        // Fetch existing patient data from Firebase
        patientRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val existingPatient = dataSnapshot.getValue(PatientData::class.java)

                    // Update only non-empty fields
                    existingPatient?.apply {
                        if (firstName.isNotEmpty()) this.firstname = firstName
                        if (lastName.isNotEmpty()) this.lastname = lastName
                        if (phone.isNotEmpty()) this.phone = phone
                        if (email.isNotEmpty()) this.email = email
                        if (username.isNotEmpty()) this.username = username
                        if (dob.isNotEmpty()) this.dob = dob
                    }

                    // Update the patient details in the Firebase Realtime Database
                    patientRef.setValue(existingPatient)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Your details updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to update patient details", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("EditPatientFragment", "Patient with ID $patientId not found in the database")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("EditPatientFragment", "Error fetching patient data from the database: $databaseError")
            }
        })
    }

}
