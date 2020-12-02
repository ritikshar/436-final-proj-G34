package com.example.studentexpensetrackersystem

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class DetailsActivity : Activity() {

    private lateinit var close: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_list)

        close = findViewById(R.id.done)

        val fileName = intent.getStringExtra("key")

        try{
            if (fileName != null) {
                readFile(fileName)
            }
        }catch(e: IOException){
            Log.i("DetailsActivity", "IOException")
        }

        close.setOnClickListener{
            finish()
        }
    }

    private fun readFile(file: String){
        val fis = openFileInput(file)
        val br = BufferedReader(InputStreamReader(fis))

        br.forEachLine{
            //ToDo this is already reading each line
            //I've split the list already so you can easily access the data
            val list = it.split(Regex(", "), 3)

        }
    }
}