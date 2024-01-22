package com.example.healthmate


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp


private lateinit var splashImg: ImageView
class SplashScreen : AppCompatActivity() {

    //companion object {
        //private const val TAG = "TestActivity"
            //}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        FirebaseApp.initializeApp(this)
        splashImg = findViewById(R.id.splashImg)

        splashImg.alpha = 0f

        //throw RuntimeException("Test Crash")



        //writeNewUser("test","test","test@gmail.com")

        splashImg.animate().setDuration(1500).alpha(1f).withEndAction {


            val i = Intent(this, RoleScreenActivity::class.java)
            startActivity(i)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }
    }







}