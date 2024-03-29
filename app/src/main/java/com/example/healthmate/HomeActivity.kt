package com.example.healthmate

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.healthmate.Patient.BookingFormFragment
import com.example.healthmate.EditPatientFragment
import com.google.android.material.navigation.NavigationView
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage


class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView = navigationView.getHeaderView(0) // Access the header view

        // Now, find the views within the header view
        val fullNameTextView = headerView.findViewById<TextView>(R.id.full_name)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profile_image)

        val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
        val fullname = sharedPreferences.getString("fullname", "")
        val imageURL = sharedPreferences.getString("imageURL", "")
        val patientType = sharedPreferences.getString("patient_type", "")
        Log.d("patientType", patientType.toString())
        val imgVip = headerView.findViewById<ImageView>(R.id.imgVip)
        if ( patientType == "VIP")
        {
            imgVip.visibility = View.VISIBLE
        }
        else{
            imgVip.visibility = View.GONE
        }


        fullNameTextView.text = fullname
        // Load and set profile image using Glide
        if (!imageURL.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageURL)
                .placeholder(R.drawable.ic_profile)
                .error(R.drawable.error_image)
                .circleCrop()
                .into(profileImageView)
        }

        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        if (savedInstanceState == null)
        {
            supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, fragment_home()).commit()
            navigationView.setCheckedItem(R.id.nav_home)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.nav_home -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, fragment_home()).commit()

            R.id.menu_profile -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, EditPatientFragment()).commit()

            R.id.nav_bookappointment -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, BookingFormFragment()).commit()

            R.id.nav_bookings -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, FragmentAppointmentsConfirm()).commit()

            R.id.nav_notifications -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, FragmentNotifications()).commit()

            R.id.nav_doctors -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, FragmentDoctorList()).commit()

            R.id.nav_chatbot -> supportFragmentManager.beginTransaction()
                .replace(R.id.patient_fragment_container, FragmentPatientChat()).commit()

            R.id.nav_logout -> logoutUser()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logoutUser(){
        Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show()
        clearSession()

        val intent = Intent(this@HomeActivity, RoleScreenActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun clearSession() {
        // Get SharedPreferences instance
        val sharedPreferences: SharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)

        // Clear the user session data
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove("MySession")
        editor.apply()
    }

    // Method to handle click on the upload icon
    fun onUploadIconClick(view: View) {
        // Open an image picker or implement your image selection logic
        // For simplicity, let's assume you're using startActivityForResult with an image picker
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }
    // onActivityResult method to handle the result of the image picker
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Use the safe call operator to handle nullable Uri
            val imageUri: Uri? = data.data

            // Check if imageUri is not null before proceeding
            imageUri?.let {
                //val userId = "unique_user_id" // Replace with the actual user ID

                val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                val userId = sharedPreferences.getString("loginid", "").toString()
                // Upload image to Firebase Storage
                uploadImage(it, userId)
            }
        }
    }

    // Function to upload image to Firebase Storage
    private fun uploadImage(imageUri: Uri, userId: String) {
        val storageRef = FirebaseStorage.getInstance().reference.child("patient_images/$userId.jpg")

        storageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                // Get the download URL
                storageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val imageUrl = downloadUri.toString()

                    // Update the user's profile image URL in the database
                    updateProfileImageUrl(userId, imageUrl) { success ->
                        if (success) {
                            val sharedPreferences = getSharedPreferences("MySession", Context.MODE_PRIVATE)
                            val editor = sharedPreferences.edit()
                            editor.putString("imageURL", imageUrl)
                            // Reload the image in the nav_header
                            loadNavHeaderImage(imageUrl)
                        }
                    }
                }
            }
            .addOnFailureListener {
                // Handle failure
                showToast("Failed to upload image. ${it.message}")
            }
    }

    private fun loadNavHeaderImage(imageUrl: String) {
        // Load and set profile image using Glide or your preferred method
        val navigationView = findViewById<NavigationView>(R.id.nav_view)
        val headerView = navigationView.getHeaderView(0)
        val profileImageView = headerView.findViewById<ImageView>(R.id.profile_image)

        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.ic_profile)
            .error(R.drawable.error_image)
            .circleCrop()
            .into(profileImageView)
    }

    private fun updateProfileImageUrl(userId: String, imageUrl: String, callback: (Boolean) -> Unit) {
        val databaseRef = FirebaseDatabase.getInstance().reference.child("patients").child(userId)

        val userDetailsUpdates = mapOf(
            "imageUrl" to imageUrl
            // Add other user details as needed
        )

        databaseRef.updateChildren(userDetailsUpdates)
            .addOnSuccessListener {
                // Handle success
                showToast("Image updated successfully.")
                callback.invoke(true)
            }
            .addOnFailureListener {
                // Handle failure
                showToast("Failed to update image. ${it.message}")
                callback.invoke(false)
            }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST_CODE = 1001
    }

    fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

}