package com.example.healthmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot


private lateinit var firstnameEditText: EditText
private lateinit var lastnameEditText: EditText
private lateinit var emailEditText: EditText
private lateinit var designationEditText: EditText
private lateinit var usernameEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var phoneEditText: EditText
private lateinit var RegisterButton: Button
private lateinit var BackButton: Button

//private lateinit var binding : ActivityMainBinding
private lateinit var database : DatabaseReference


class DrSignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dr_sign_up)

        supportActionBar!!.hide() // to hide the app bar

        firstnameEditText = findViewById(R.id.editTextFirstname)
        lastnameEditText = findViewById(R.id.editTextLastname)
        emailEditText = findViewById(R.id.edittextEmailAddress)
        designationEditText = findViewById(R.id.editTextDesignation)
        usernameEditText = findViewById(R.id.edittextUsername)
        passwordEditText = findViewById(R.id.edittextPassword)
        phoneEditText = findViewById(R.id.edittextPhone)
        RegisterButton = findViewById(R.id.btnRegisterPtSignUp)
        BackButton = findViewById(R.id.btnBack)


        BackButton.setOnClickListener {
            val intent = Intent(this@DrSignUpActivity, DrSignIn_Activity::class.java)
            startActivity(intent)
            finish()
        }
        RegisterButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString();
            val fname = firstnameEditText.text.toString();
            val lname = lastnameEditText.text.toString();
            val designation = designationEditText.text.toString();
            val email = emailEditText.text.toString();
            val phone = phoneEditText.text.toString();

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
            if (designation.isEmpty()) {
                designationEditText.error = "Please enter designation"
                hasError = true
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
                    database.getReference("doctors") // Replace "users" with your desired database node

                ///////// isDoctorExists
                checkUsernameAvailability(username) { isAvailable ->
                    if (isAvailable) {
                        // Username is available, proceed with registration or other actions
                        registerUser(username, email, password, fname, lname, phone, designation)
                        // ...
                    } else {
                        // Username is not available, show an error toast
                        showToast("Username is not available")
                    }
                }

            }

        }


    }

    fun registerUser(username: String, email: String,password: String, fname: String, lname: String, phone: String, designation: String) {

        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("doctors")

        generateUniqueUserId(reference) { uniqueUserId ->
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Authentication successful, proceed to add data to Firebase
                        // Create a User object or a map with user data
                        val userData = mapOf(
                            "drId" to uniqueUserId,
                            "username" to username,
                            "email" to email,
                            "password" to password,
                            "firstname" to fname,
                            "lastname" to lname,
                            "phone" to phone,
                            "designation" to designation,
                            "imageUrl" to ""
                        )

                        // Add user data to Firebase
                        reference.child(uniqueUserId).setValue(userData)

                        showToast("Registration successful")

                        val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                        val editor = sharedPreferences.edit()

                        // Assuming userToken is the session value you want to store
                        editor.putString("username", username)
                        editor.putString("loginid", uniqueUserId)
                        editor.putString("imageUrl", uniqueUserId)
                        editor.apply()

                        // Authentication successful, navigate to the welcome activity
                        val intent = Intent(this@DrSignUpActivity, DrHomeActivity::class.java)
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
        val reference = database.getReference("doctors") // Replace "users" with your database node

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
        val userId = "dr${System.currentTimeMillis()}"

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



}