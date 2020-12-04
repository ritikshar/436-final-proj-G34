package com.example.studentexpensetrackersystem

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.util.ArrayList

class DetailsActivity : AppCompatActivity() {

    private lateinit var close: Button
    private lateinit var listView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.i(TAG, "DetailsActivity.onCreate()")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.display_list)

        listView = findViewById(R.id.listview)
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
        //val br = BufferedReader(InputStreamReader(fis))
        val lst = BufferedReader(InputStreamReader(fis)).readLines() as ArrayList<String>



//        br.forEachLine{
//            //ToDo this is already reading each line
//            //I've split the list already so you can easily access the data
//            Log.i(TAG, "lets see how many we get here $it")
//        }
        Log.i(TAG, "DetailsActivity.readFile()")


        Log.i(TAG, "We got a list now" + lst.size)
        for(ele in lst){
            Log.i(TAG, "Printing element: $ele")
        }
        listView.adapter = SpendingAdapter(this, lst)
        fis.close()
    }
    companion object {
        private val TAG = "Expense-Tracker"
    }
}