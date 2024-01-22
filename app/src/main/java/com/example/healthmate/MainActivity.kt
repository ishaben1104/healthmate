package com.example.healthmate

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


private lateinit var emailEditText: EditText
private lateinit var passwordEditText: EditText
private lateinit var loginButton: Button
private lateinit var btnNewUser: Button


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        emailEditText = findViewById(R.id.edittextEmailAddress)
        passwordEditText = findViewById(R.id.edittextPassword)
        loginButton = findViewById(R.id.btnLogin)
        btnNewUser = findViewById(R.id.btnNewUser)

        supportActionBar!!.hide() // to hide the app bar

        btnNewUser.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener(View.OnClickListener {

            // Get the entered username and password
            val username = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            //checking on firebase db
            login(username, password) { isSuccess ->
                if (isSuccess) {
                    // Login successful, navigate to the next screen or perform other actions
                    showToast("Login successful!")
                    val intent = Intent(this@MainActivity, HomeActivity::class.java)
                    startActivity(intent)
                } else {
                    showToast("Incorrect login information!")
                    // Login failed, show an error message
                }
            }

        }
        )

    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    // Function to check username availability
    fun login(username: String, password: String, callback: (Boolean) -> Unit) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.getReference("patients") // Replace "doctors" with your database node

        val loginQuery = reference.orderByChild("username").equalTo(username)

        loginQuery.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Username exists, check password
                    for (userSnapshot in dataSnapshot.children) {
                        val storedPassword = userSnapshot.child("password").getValue(String::class.java)

                        if (password == storedPassword) {
                            val userId = userSnapshot.key // Retrieve the unique user ID
                            val patient_type = userSnapshot.child("patient_type").getValue(String::class.java)
                            val firstname = userSnapshot.child("firstname").getValue(String::class.java)
                            val lastname = userSnapshot.child("lastname").getValue(String::class.java)
                            val imageUrl = userSnapshot.child("imageUrl").getValue(String::class.java)

                            val fullNm = "$firstname $lastname"
                            // Set user information in session
                            val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()

                            // Assuming userToken is the session value you want to store
                            editor.putString("username", username)
                            editor.putString("patient_type", patient_type)
                            editor.putString("fullname", fullNm)
                            editor.putString("imageURL", imageUrl)
                            editor.putString("loginid", userId)
                            editor.putString("otpcode", "")
                            editor.apply()

                            // Passwords match, login successful
                            callback(true)
                        } else {
                            // Passwords do not match
                            callback(false)
                        }
                    }
                } else {
                    // Username does not exist
                    callback(false)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle database error (optional)
                callback(false) // Assume login failure on error
            }
        })
    }

}