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

/*
PinSettingActivity: This is the activity where the user can either
                    delete the new pin or create a new pin for the app.
 */

class PinSettingActivity : AppCompatActivity(){

    private lateinit var pinString: String
    private lateinit var text: TextView
    private lateinit var sharedPref: SharedPreferences

    /*
    If there is a pin already, the function asks the user if they want
    to delete the existing pin after entering their current pin.
    If there is no pre-existing pin, the user can create a new pin
    by entering the same pin twice.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.pin_setting)
        sharedPref = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        pinString = ""
        text = findViewById(R.id.pinText)
        text.text = "Enter your desired pin"
        val view = findViewById<Pinview>(R.id.pinview)
        if(!sharedPref.getBoolean(LOGIN, false)) {
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
        }else{
            text.text = "Enter your current pin"
            view.setPinViewEventListener { pin, _ ->
                if (pin.value == sharedPref.getString("password", null).toString()) {
                    val manager = RemovePinFragment()
                    manager.show(supportFragmentManager, "remove")
                }else{
                    text.text = "Wrong pin entered"
                    pin.clearValue()
                }
            }
        }
    }

    companion object{
        private const val myPreference = "mypref"
        private const val LOGIN = "userLoginPreference"
    }
}