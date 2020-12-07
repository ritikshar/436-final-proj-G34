package com.example.studentexpensetrackersystem

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.io.*
import java.util.ArrayList
import kotlin.math.abs

/*
DetailsActivity: This is the activity to display the listView for both the details
                and the delete expense button. For the details button, it is just
                supposed to display the list while for the delete, it deletes
                items from the list. It uses a custom adapter called SpendingAdapter.
 */

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

    /*
    @file: Name of the text file
    @isDelete: Boolean indicating if the list view is for delete or details
    Function is used read from the text file that was created to store the
    expenses. It will create and instance of adapter to display the items
    and if isDelete is true, it also starts the delete on longClick.
     */
    private fun readFile(file: String, isDelete: Boolean){
        val lst = getFileStreamPath(file).readLines() as ArrayList<String>

        Log.i(TAG, "DetailsActivity.readFile()")


        Log.i(TAG, "We got a list now" + lst.size)
        for(ele in lst){
            Log.i(TAG, "Printing element: $ele")
        }
        listView.adapter = SpendingAdapter(this, lst)
        if(isDelete){
            listView.onItemLongClickListener =
                AdapterView.OnItemLongClickListener { parent, view, position, id ->

                    val lineToRemove = lst[position]
                    lst.remove(lineToRemove)
                    val currLineLst = lineToRemove.split(Regex(", "), 3)
                    if (currLineLst.size != 1)
                        applyDeleting(currLineLst[0], currLineLst[1])
                    (listView.adapter as SpendingAdapter).notifyChange()
                }
        }
        (listView.adapter as SpendingAdapter).notifyDataSetChanged()
    }

    /*
    @category: The category of the expense to delete
    @price: The price of the expense to delete
    The function is called from the previous readFile function
    if an item needs to be deleted. The item needs to be found
    in the text file and removed while also updating the total
    expense along with the over/under calculation.
     */
    fun applyDeleting(category: String?, price: String?) {
        if (category != null && price != null) {
            var flag = 0
            var lines = getFileStreamPath(MainActivity.FILE_NAME).readLines()
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
                    val spent = -price.toFloat()
                    MainActivity.total_spent -= spent
                    MainActivity.total_spending_value.text = MainActivity.total_spent.format(2)
                    MainActivity.sharedpreferences.edit().putFloat(MainActivity.TOTAL, MainActivity.total_spent).apply()
                    //over under calculation here
                        if (MainActivity.budget > MainActivity.total_spent) {
                            MainActivity.over_under = MainActivity.budget - MainActivity.total_spent
                            MainActivity.over_under_text.setTextColor(Color.parseColor("#006400"))

                            val text = "UNDER $" + MainActivity.over_under.format(2) + " of the current Budget!"

                            MainActivity.over_under_text.text = text
                        }
                        else if (MainActivity.budget < MainActivity.total_spent) {
                            MainActivity.over_under = MainActivity.total_spent - MainActivity.budget
                            MainActivity.over_under_text.setTextColor(Color.RED)

                            val text = "OVER $" +  MainActivity.over_under.format(2) + " of the current Budget!"

                            MainActivity.over_under_text.text = text
                        }
                        else { //budget == total_spent
                            MainActivity.over_under = 0.toFloat()
                            MainActivity.over_under_text.setTextColor(Color.parseColor("#006400"))
                            MainActivity.over_under_text.text = "You are at your current Budget!"
                        }

                        MainActivity.sharedpreferences.edit().putFloat(MainActivity.OVER_UNDER, MainActivity.over_under).apply()

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

    companion object {
        private val TAG = "Expense-Tracker"
    }
}