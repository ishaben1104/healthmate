package com.example.healthmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import android.util.Log

class PaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        supportActionBar!!.hide(); // to hide the app bar

        val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", "")
        val patientType = sharedPreferences.getString("patient_type", "")
        val paidAmount = sharedPreferences.getString("paidamount", "")
        val patientId = sharedPreferences.getString("loginid", "")

        val etCreditCardNumber = findViewById<EditText>(R.id.etCreditCardNumber)
        val hdrAmount = findViewById<TextView>(R.id.hdrAmount)
        val etExpirationMonth = findViewById<EditText>(R.id.etExpirationMonth)
        val etExpirationYr = findViewById<EditText>(R.id.etExpirationYear)
        val etCVV = findViewById<EditText>(R.id.etCVV)
        val btnSubmit = findViewById<Button>(R.id.btnSubmit)

        hdrAmount.text = "Amount to be paid: £" + paidAmount.toString()

        btnSubmit.setOnClickListener {
            val creditCardNumber = etCreditCardNumber.text.toString()
            val expirationMonth = etExpirationMonth.text.toString()
            val ExpirationYr = etExpirationYr.text.toString()
            val cvv = etCVV.text.toString()

            Log.d(
                "PaymentActivity",
                "Credit Card Number Valid: ${isValidCreditCard(creditCardNumber)}"
            )
            Log.d(
                "PaymentActivity",
                "Expiration Valid: ${isValidMonthAndYear(expirationMonth, ExpirationYr)}"
            )
            Log.d("PaymentActivity", "CVV Valid: ${isValidCVV(cvv)}")

            if (creditCardNumber.isEmpty() || expirationMonth.isEmpty() || ExpirationYr.isEmpty() || cvv.isEmpty() ) {
                showToast("All fields are required")
            } else {
                if (isValidCreditCard(creditCardNumber) && isValidMonthAndYear(
                        expirationMonth,
                        ExpirationYr
                    ) && isValidCVV(cvv)
                ) {
                    // All fields are valid, you can proceed with further actions
                    // For now, let's just display a toast message
                    // Retrieve patient ID from SharedPreferences

                    if (patientId.isNullOrEmpty()) {
                        showToast("Patient ID not found in session.")
                    } else {
                        // Now, use patientId in your Firebase update logic
                        val database = FirebaseDatabase.getInstance()
                        val reference = database.getReference("patients").child(patientId)

                        // ... Firebase update logic
                        // Create a map with the field(s) you want to update
                        val updates = HashMap<String, Any>()
                        updates["payment_status"] =
                            "success" // Replace with your actual field and value

                        // Update the specific fields in the patient node
                        reference.updateChildren(updates)
                            .addOnSuccessListener {
                                showToast("Payment done successfully.")
                            }
                            .addOnFailureListener {
                                showToast("Failed to update payment. ${it.message}")
                            }
                        // Payment mode, navigate to the welcome activity
                        val intent = Intent(this@PaymentActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }

                    showToast("Data is valid!")
                } else {
                    showToast("Invalid data. Please enter valid information.")
                }
            }

        }
    }

    private fun isValidCreditCard(creditCardNumber: String): Boolean {
        // Basic validation: Check if the credit card number is numeric and has 16 digits -- 4111111111111111
//      return creditCardNumber.isNotEmpty() && creditCardNumber.matches(Regex("\\d{16}"))
        if (creditCardNumber.isEmpty() || !creditCardNumber.matches(Regex("\\d{16}"))) {
            return false
        }

        val blockedCardNumbers =
            listOf("1234567890123456", "9876543210987654", "1111222233334444", "4111111111111111")
        if (blockedCardNumbers.contains(creditCardNumber)) {
            val etCreditCardNumber = findViewById<EditText>(R.id.etCreditCardNumber)
            etCreditCardNumber.error = "Credit card is blocked or incorrect!"
            return false
        }

// The credit card number passed all validations
        return true
    }

    private fun isValidCVV(cvv: String): Boolean {
        // Basic validation: Check if the CVV is a valid three-digit number
        return cvv.isNotEmpty() && cvv.matches(Regex("\\d{3}"))
    }

    private fun isValidMonthAndYear(month: String, year: String): Boolean {
        // Basic validation: Check if the month is a valid two-digit number (01-12)
        if (month.isEmpty() || !month.matches(Regex("^(0[1-9]|1[0-2])$"))) {
            return false
        }

        // Basic validation: Check if the year is a valid four-digit number
        if (year.isEmpty() || !year.matches(Regex("^\\d{4}$"))) {
            return false
        }

        // Additional validation: Check if the date is after or equal to the current month and year
        val currentDate = Calendar.getInstance()

        val enteredDate = Calendar.getInstance().apply {
            set(Calendar.MONTH, month.toInt() - 1) // Calendar months are 0-based
            set(Calendar.YEAR, year.toInt())
            set(Calendar.DAY_OF_MONTH, getActualMaximum(Calendar.DAY_OF_MONTH))
        }

        return enteredDate.after(currentDate)
    }

    private fun showToast(message: String) {
        // Display a toast message (you can replace this with your desired UI feedback)
        // For simplicity, I'm not using a Toast in this example
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        println(message)
    }

}