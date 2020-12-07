package com.example.studentexpensetrackersystem

import android.widget.EditText
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.goodiebag.pinview.Pinview
import kotlinx.android.synthetic.main.activity_login.*


/*
LogInActivity: This is the first activity the user sees if they have a pin created.
                They have to enter a pin to access the application.
 */

class LogInActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pinCheck()
    }

    //Funtion that checks if the pin entered is correct.
    private fun pinCheck(){
        val userPin = intent.getStringExtra(key)
        val view = findViewById<Pinview>(R.id.pinview)
        view.setPinViewEventListener { pin, _ ->
            if (pin.value != userPin) {
                Toast.makeText(this@LogInActivity,
                                "Incorrect Pin Entered",
                                Toast.LENGTH_SHORT).show()
                pin.clearValue()
            }else{
                finish()
            }
        }
    }

    companion object{
        private const val key = "password"
    }
}