
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

class FragmentDrUpdateProfile : Fragment() {

    private lateinit var databaseReference: DatabaseReference
    private lateinit var drRef: DatabaseReference
    private lateinit var editTextFirstname: AppCompatEditText
    private lateinit var editTextLastname: AppCompatEditText
    private lateinit var edittextPhone: AppCompatEditText
    private lateinit var edittextEmailAddress: AppCompatEditText
    private lateinit var edittextUsername: AppCompatEditText
    private lateinit var edittextDesignation: AppCompatEditText
    private lateinit var edittextPassword: AppCompatEditText
    private lateinit var btnUpdatePtProfile: Button
    private val sharedPreferencesKey = "MySession"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dr_update_profile, container, false)

        databaseReference = FirebaseDatabase.getInstance().getReference("doctors")

        editTextFirstname = view.findViewById(R.id.editTextFirstname_dr)
        editTextLastname = view.findViewById(R.id.editTextLastname_dr)
        edittextPhone = view.findViewById(R.id.edittextPhone_dr)
        edittextEmailAddress = view.findViewById(R.id.edittextEmailAddress_dr)
        edittextUsername = view.findViewById(R.id.edittextUsername_dr)
        edittextDesignation = view.findViewById(R.id.edittextDesignation_dr)
        btnUpdatePtProfile = view.findViewById(R.id.btnUpdatePtProfile)

        val sharedPreferences: SharedPreferences =
            requireContext().getSharedPreferences(sharedPreferencesKey, Context.MODE_PRIVATE)
        val userId: String = sharedPreferences.getString("loginid", null).toString()
        Log.d("Userids", "Name: ${userId}")
        drRef = FirebaseDatabase.getInstance().getReference("doctors").child(userId)

        // read data
        readDataFromFirebase(userId)

        btnUpdatePtProfile.setOnClickListener {
            // Update doctors data on Firebase
            updateDrData(userId)
        }

        return view
    }

    private fun readDataFromFirebase(drId: String) {
        val drRef = databaseReference.child(drId)

        drRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val patientData = snapshot.getValue(PatientData::class.java)

                if (patientData != null) {
                    editTextFirstname.text = Editable.Factory.getInstance().newEditable(patientData.firstname)
                    editTextLastname.text = Editable.Factory.getInstance().newEditable(patientData.lastname)
                    edittextPhone.text = Editable.Factory.getInstance().newEditable(patientData.phone)
                    edittextEmailAddress.text = Editable.Factory.getInstance().newEditable(patientData.email)
                    edittextUsername.text = Editable.Factory.getInstance().newEditable(patientData.username)
                    edittextDesignation.text = Editable.Factory.getInstance().newEditable(patientData.dob)

                    Log.d("FirebaseData", "DrName: ${patientData.firstname}, Email: ${patientData.lastname}")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("FirebaseData", "Failed to read value.", error.toException())
            }
        })
    }

    private fun updateDrData(drId: String) {
        val firstName = editTextFirstname.text.toString()
        val lastName = editTextLastname.text.toString()
        val phone = edittextPhone.text.toString()
        val email = edittextEmailAddress.text.toString()
        val username = edittextUsername.text.toString()
        val designation = edittextDesignation.text.toString()
        val password = edittextPassword.text.toString()

        // Create a PatientData object with the updated details
        if (password.isNotEmpty())
        {
            val updatedDr = DrData(drId, firstName, lastName, phone, email, username, password, designation)
            // Update the doctor details in the Firebase Realtime Database
            drRef.setValue(updatedDr)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Your details updated successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update details", Toast.LENGTH_SHORT).show()
                }
        }
        else {
            val updatedPatient = DrDataNoPwd(drId, firstName, lastName, phone, email, username, designation)
            // Update the doctor details in the Firebase Realtime Database
            drRef.setValue(updatedPatient)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Your details and password updated successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Failed to update your details and password ", Toast.LENGTH_SHORT).show()
                }
        }


    }


}
