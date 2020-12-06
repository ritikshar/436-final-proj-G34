package com.example.studentexpensetrackersystem

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.goodiebag.pinview.Pinview
import java.lang.NullPointerException

class PinSettingActivity : AppCompatActivity(){

    private lateinit var pinString: String
    private lateinit var text: TextView
    private lateinit var sharedPref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_setting)
        sharedPref = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        pinString = ""
        text = findViewById(R.id.pinText)
        text.text = "Enter your desired pin"
        val view = findViewById<Pinview>(R.id.pinview)
//        if(!sharedPref.getBoolean(LOGIN, false)) {
            view.setPinViewEventListener { pin, _ ->
                if (pinString != "") {
                    if (pinString == pin.value) {
                        sharedPref.edit().putBoolean(LOGIN, true).apply()
                        sharedPref.edit().putString("password", pin.value).apply()
                        Toast.makeText(this,"New Pin Set!", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        pin.clearValue()
                        pinString = ""
                        text.text = "Enter your desired pin"
                    }
                } else {
                    pinString = pin.value
                    pin.clearValue()
                    text.text = "Reenter your desired pin"
                }
            }
//        }else{
//            text.text = "Enter your current pin"
//            view.setPinViewEventListener { pin, _ ->
//                if (pin.value == sharedPref.getString("password", null).toString()) {
//
//                }
//            }
//        }
    }

    private fun pinCheck(){
        text.text = "Reenter your desired pin"
        val view = findViewById<Pinview>(R.id.pinview)
        view.setPinViewEventListener { pin, _ ->
            if (pin.value != pinString) {
                text.text = "Incorrect Pin Entered"
                pin.clearValue()
            }else{
                finish()
            }
        }
    }

    companion object{
        private const val myPreference = "mypref"
        private const val LOGIN = "userLoginPreference"
    }
}