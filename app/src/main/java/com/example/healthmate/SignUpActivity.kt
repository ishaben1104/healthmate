package com.example.healthmate

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.ParseException
import java.text.SimpleDateFormat
import android.widget.DatePicker
import java.util.Calendar
import java.util.Date
import java.util.Locale

private lateinit var firstnameEditText: EditText
private lateinit var lastnameEditText: EditText
private lateinit var emailEditText: EditText
private lateinit var dobEditText: EditText
private lateinit var usernameEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var phoneEditText: EditText
private lateinit var RegisterButton: Button
private lateinit var BackButton: Button
private var sessPayAmount: Int = 50



class SignUpActivity : AppCompatActivity() {
//    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val switchVIP = findViewById<Switch>(R.id.switchVIP)
        val textViewVIPStatus = findViewById<TextView>(R.id.textViewVIPStatus)
        var PatientType = "Normal"

        switchVIP.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                textViewVIPStatus.text = "VIP patient type pays yearly payment of £350, covers dental treatment or minor surgery."
                PatientType = "VIP"
                sessPayAmount = 350
            } else {
                textViewVIPStatus.text = "Normal patient type pays yearly payment of £50, where they need to pay extra for any dental treatment or surgery."
                PatientType = "Normal"
                sessPayAmount = 50
            }
        }

        supportActionBar!!.hide() // to hide the app bar

        firstnameEditText = findViewById(R.id.editTextFirstname)
        lastnameEditText = findViewById(R.id.editTextLastname)
        emailEditText = findViewById(R.id.edittextEmailAddress)
        dobEditText = findViewById(R.id.edittextDOB)
        usernameEditText = findViewById(R.id.edittextUsername)
        passwordEditText = findViewById(R.id.edittextPassword)
        phoneEditText = findViewById(R.id.edittextPhone)
        RegisterButton = findViewById(R.id.btnRegisterPtSignUp)
        BackButton = findViewById(R.id.btnBack)

        BackButton.setOnClickListener {
            val intent = Intent(this@SignUpActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        RegisterButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()
            val fname = firstnameEditText.text.toString()
            val lname = lastnameEditText.text.toString()
            val dob = dobEditText.text.toString()
            val email = emailEditText.text.toString()
            val phone = phoneEditText.text.toString()

            var hasError = false

            if (fname.isEmpty()) {
                firstnameEditText.error = "Please enter firstname"
                hasError = true
            }
            if (lname.isEmpty()) {
                lastnameEditText.error = "Please enter lastname"
                hasError = true
            }

            val emailRegex = Regex("[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
            val isValidEmail = email.matches(emailRegex)
            if (email.isEmpty()) {
                emailEditText.error = "Please enter email"
                hasError = true
            }
            else if (!isValidEmail) {
                emailEditText.error = "Please enter a valid email address"
                hasError = true
            }
            if (dob.isEmpty()) {
                dobEditText.error = "Please enter date of birth"
                hasError = true
            }
            else {
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                try {
                    val dateOfBirth = sdf.parse(dob)
                    val currentDate = Date()

                    if (dateOfBirth != null && dateOfBirth.after(currentDate)) {
                        dobEditText.error = "Date of birth should be before today"
                        hasError = true
                    }
                } catch (e: ParseException) {
                    e.printStackTrace()
                    dobEditText.error = "Invalid date format. Please use MM/dd/yyyy"
                    hasError = true
                }
            }
            if (username.isEmpty()) {
                usernameEditText.error = "Please enter username"
                hasError = true
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Please enter password"
                hasError = true
            }
            else if (password.isEmpty())
            {
                passwordEditText.error = "Please enter atleast 6 digits of password!"
                hasError = true
            }
            if (phone.isEmpty()) {
                phoneEditText.error = "Please enter password"
                hasError = true
            } else {
                if (phone.length < 10 || phone.length > 12 ) {
                    phoneEditText.error = "Phone length can neither less than 10 nor greater than 12"
                    hasError = true
                }
            }

            if (!hasError) {
                RegisterButton.isEnabled = true
                val database = FirebaseDatabase.getInstance()
                val reference =
                    database.getReference("patients") // Replace "users" with your desired database node

                ///////// isPatientExists
                checkUsernameAvailability(username) { isAvailable ->
                    if (isAvailable) {
                        // Username is available, proceed with registration or other actions
                        registerUser(username, email, password, fname, lname, phone, dob, PatientType, sessPayAmount)
                        // ...
                    } else {
                        // Username is not available, show an error toast
                        showToast("Username is not available")
                    }
                }

            }

        }


    }

    fun registerUser(username: String, email: String,password: String, fname: String, lname: String, phone: String, dob: String, patient_type: String, paidAmount: Int) {

        val randomCode = (1000..9999).random()
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("patients")

        generateUniqueUserId(reference) { uniqueUserId ->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Authentication successful, proceed to add data to Firebase
                        // Create a User object or a map with user data
                        val userData = mapOf(
                            "ptId" to uniqueUserId,
                            "username" to username,
                            "email" to email,
                            "password" to password,
                            "firstname" to fname,
                            "lastname" to lname,
                            "phone" to phone,
                            "dob" to dob,
                            "amount" to paidAmount,
                            "patient_type" to patient_type,
                            "payment_status" to "Pending",
                            "otpcode" to randomCode
                        )

                        // Add user data to Firebase
                        reference.child(uniqueUserId).setValue(userData)

                        showToast("Registration successful")

                        val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        val fullNm = "$fname $lname"
                        // Assuming userToken is the session value you want to store
                        editor.putString("username", username)
                        editor.putString("patient_type", patient_type)
                        editor.putString("paidamount", paidAmount.toString())
                        editor.putString("loginid", uniqueUserId)
                        editor.putString("otpcode", randomCode.toString())
                        editor.putString("fullname", fullNm)
                        editor.apply()

//                        try {
//                            sendOTPEmail(email, randomCode.toString())
//                        }
//                        catch (e: MessagingException) {
//                            showToast("Some Error")
//                    }
                        // Authentication successful, navigate to the welcome activity
                        val intent = Intent(this@SignUpActivity, VerifyActivity::class.java)
                        startActivity(intent)


                    } else {
                        // Authentication failed
                        showToast("Registration failed: ${task.exception?.message}")
                    }
                }
        }


    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // Function to check username availability
    fun checkUsernameAvailability(usernameToCheck: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("patients") // Replace "users" with your database node

        val usernameQuery = reference.orderByChild("username").equalTo(usernameToCheck)

        usernameQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                callback(!dataSnapshot.exists()) // Pass the result to the callback function
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error (optional)
                callback(false) // Assume username is not available on error
            }
        })
    }

    fun generateUniqueUserId(reference: DatabaseReference, callback: (String) -> Unit) {
        // Generate a unique user ID based on the current timestamp
        val userId = "${System.currentTimeMillis()}"

        // Check if the generated ID already exists
        reference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // If ID already exists, recursively call the function to generate a new one
                    generateUniqueUserId(reference, callback)
                } else {
                    // If ID is unique, pass it to the callback
                    callback(userId)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error (optional)
                callback(userId) // Assume ID is unique on error
            }
        })
    }

    fun showDatePicker(view: View) {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            this,
            R.style.DatePickerDialogTheme,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.US)
                dobEditText = findViewById(R.id.edittextDOB)
                dobEditText.setText(sdf.format(selectedDate.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        // Set the maximum allowed date to the current date
        datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

        datePickerDialog.show()
    }

//    fun sendOTPEmail(email: String, otp: String) {
//        // Set your Gmail credentials
//        val username = "meemansa.goswami2179@gmail.com"
//        val password = "Bhavyesh1432"
//
//        // Set properties for the SMTP server
//        val properties = Properties().apply {
//            put("mail.smtp.auth", "true")
//            put("mail.smtp.starttls.enable", "true")
//            put("mail.smtp.host", "smtp.gmail.com")
//            put("mail.smtp.port", "587")
//        }
//
//        // Create a session with the SMTP server
//        val session = Session.getInstance(properties, object : Authenticator() {
//            override fun getPasswordAuthentication(): PasswordAuthentication {
//                return PasswordAuthentication(username, password)
//            }
//        })
//
//        try {
//            // Create a MimeMessage object
//            val message = MimeMessage(session)
//
//            // Set sender and recipient addresses
//            message.setFrom(InternetAddress(username))
//            message.addRecipient(Message.RecipientType.TO, InternetAddress(email))
//
//            // Set email subject and body
//            message.subject = "Your OTP Code"
//            message.setText("Your OTP code is: $otp")
//
//            // Send the email
//            Transport.send(message)
//
//            showToast("Email sent successfully")
//            println("Email sent successfully")
//
//        } catch (e: MessagingException) {
//            showToast("Error")
//            e.printStackTrace()
//        }
//    }

}