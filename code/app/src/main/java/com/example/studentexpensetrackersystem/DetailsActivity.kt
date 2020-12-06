package com.example.studentexpensetrackersystem

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.util.ArrayList
import kotlin.math.abs

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
        val isDelete = intent.extras?.getBoolean("delete")

        try{
            if (fileName != null) {
                if (isDelete != null) {
                    readFile(fileName, isDelete)
                }
            }
        }catch(e: IOException){
            Log.i("DetailsActivity", "IOException")
        }

        close.setOnClickListener{
            finish()
        }
    }

    private fun readFile(file: String, isDelete: Boolean){
        //val fis = openFileInput(file)
        //val br = BufferedReader(InputStreamReader(fis))
        //val lst = BufferedReader(InputStreamReader(fis)).readLines() as ArrayList<String>
        val lst = getFileStreamPath(MainActivity.FILE_NAME).readLines() as ArrayList<String>

        Log.i(TAG, "DetailsActivity.readFile()")


        Log.i(TAG, "We got a list now" + lst.size)
        for(ele in lst){
            Log.i(TAG, "Printing element: $ele")
        }
        listView.adapter = SpendingAdapter(this, lst)
        if(isDelete){
            //listView.onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
            listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { parent, view, position, id ->

                    val lineToRemove = lst[position]
                    lst.remove(lineToRemove)
                    val currLineLst = lineToRemove.split(Regex(", "), 3)
                    applyDeleting(currLineLst[0], currLineLst[1])
                    (listView.adapter as SpendingAdapter).notifyChange()
                }
        }
        (listView.adapter as SpendingAdapter).notifyDataSetChanged()
    }

    fun applyDeleting(category: String?, price: String?) {
        if (category != null && price != null) {
            var flag = 0
            val refinedPrice = price.toFloat().format(2)
            var lines = getFileStreamPath(MainActivity.FILE_NAME).readLines()
            var currLine : String
            var count = 0;
            Log.i(TAG, "CATEGORY: $category PRICE: $price")
            for(line in lines) {
                Log.i(TAG, "Inside the FOR LOOP")
                val n = lines.size
                val x = line.split(Regex(", "), 3)
                Log.i(TAG, "X[0]: "+ x[0] + " x[1]: " + x[1])
                if (x[0] == category && x[1] == price) { //found line to delete
                    Log.i(TAG, "Inside the IF STATEMENT")
                    flag = 1
                    val spent = price.toFloat()
                    //TODO: Have to set the total spending properly here
//                    Log.i(TAG, "TOTAL SPENT: " + MainActivity.total_spent)
//                    Log.i(TAG, "SPENT PRICE: $spent")
//                    MainActivity.total_spent -= spent
                    MainActivity.total_spending_value.text = MainActivity.total_spent.format(2)
                    MainActivity.sharedpreferences.edit().putFloat(MainActivity.TOTAL, MainActivity.total_spent).apply()
                    //TODO: Have to do the over under calculation here
                    //MainActivity.overUnderCalculation()
                    lines = lines.take(count) + lines.drop(count+n)
                    val text = lines.joinToString(System.lineSeparator())

                    val newText = text+"\n"
                    getFileStreamPath(MainActivity.FILE_NAME).writeText(newText)
                    break
                }
                count++
            }
            if (flag == 0) {
                Toast.makeText(this,
                    "Expense Not Found!",
                    Toast.LENGTH_LONG).show()
            }
            else if (flag == 1) {
                Toast.makeText(this,
                    "Expense Deleted!",
                    Toast.LENGTH_LONG).show()
            }
        }

    }
    fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }
    private fun write(fileName: String, text: String){

        val fos = openFileOutput(fileName, Context.MODE_PRIVATE)
        val pw = PrintWriter(BufferedWriter(OutputStreamWriter(fos)))

        pw.println(text)

        pw.close()


        Log.i("Write", "File creation success")
    }

    companion object {
        private val TAG = "Expense-Tracker"
    }
}