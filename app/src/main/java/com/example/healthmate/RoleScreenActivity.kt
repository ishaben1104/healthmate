package com.example.healthmate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.ImageButton
import android.view.animation.Animation


private lateinit var imgBtnDr: ImageButton
private lateinit var imgBtnPt: ImageButton

class RoleScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_role_screen)

        supportActionBar!!.hide(); // to hide the app bar

        imgBtnDr = findViewById(R.id.imgBtnDr)
        imgBtnPt = findViewById(R.id.imgBtnPt)

        val scaleAnimation: Animation = AnimationUtils.loadAnimation(this, R.anim.button_scale)

        imgBtnPt.setOnClickListener(View.OnClickListener {

            imgBtnPt.startAnimation(scaleAnimation)

            // Create an Intent to navigate to the next activity (NextActivity)
            val intent = Intent(this@RoleScreenActivity, MainActivity::class.java)
            startActivity(intent)
        })

        imgBtnDr.setOnClickListener(View.OnClickListener {
            imgBtnDr.startAnimation(scaleAnimation)
            // Create an Intent to navigate to the next activity (NextActivity)
            val intent = Intent(this@RoleScreenActivity, DrSignIn_Activity::class.java)
            startActivity(intent)
        })
    }
}