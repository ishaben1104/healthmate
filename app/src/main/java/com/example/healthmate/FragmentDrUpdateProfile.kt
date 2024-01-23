
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
        Log.d("Userids Doc", "Name: ${userId}")
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
        val drRefe = databaseReference.child(drId)

        drRefe.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val doctorData = snapshot.getValue(Doctor::class.java)

                if (doctorData != null) {
                    editTextFirstname.setText(doctorData.firstname)
                    editTextLastname.setText(doctorData.lastname)
                    edittextPhone.setText(doctorData.phone)
                    edittextEmailAddress.setText(doctorData.email)
                    edittextUsername.setText(doctorData.username)
                    edittextDesignation.setText(doctorData.designation)

                    Log.d("FirebaseData", "DrName: ${doctorData.firstname}, Email: ${doctorData.lastname}")
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

        // Fetch existing patient data from Firebase
        drRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val existingDoctor = dataSnapshot.getValue(Doctor::class.java)

                    // Update only non-empty fields
                    existingDoctor?.apply {
                        if (firstName.isNotEmpty()) this.firstname = firstName
                        if (lastName.isNotEmpty()) this.lastname = lastName
                        if (phone.isNotEmpty()) this.phone = phone
                        if (email.isNotEmpty()) this.email = email
                        if (username.isNotEmpty()) this.username = username
                        if (designation.isNotEmpty()) this.designation = designation
                    }

                    // Update the patient details in the Firebase Realtime Database
                    drRef.setValue(existingDoctor)
                        .addOnSuccessListener {
                            Toast.makeText(requireContext(), "Your details updated successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener {
                            Toast.makeText(requireContext(), "Failed to update patient details", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Log.e("FragmentDrUpdateProfile", "Patient with ID $drId not found in the database")
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.e("FragmentDrUpdateProfile", "Error fetching patient data from the database: $databaseError")
            }
        })
    }

}
