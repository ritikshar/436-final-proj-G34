package com.example.studentexpensetrackersystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.ArrayList

class MainActivity : AppCompatActivity(), UpdateBudgetFragment.UpdateBudgetListener, AddExpenseFragment.UpdateSpendingListener, DeleteExpenseFragment.UpdateDeletingListener {

    private lateinit var current_budget_value: TextView
    private lateinit var update_budget: Button
    private lateinit var total_spending_value: TextView
    private lateinit var add_expense: Button
    private lateinit var over_under_text: TextView
    private lateinit var details: Button
    private lateinit var updateBudgetDialog: DialogFragment
    private lateinit var addExpenseDialog: DialogFragment
    private lateinit var deleteExpenseDialog: DialogFragment
    private lateinit var sharedpreferences: SharedPreferences
    private lateinit var file: File


    //private var data = mutableMapOf<String, Float>()

    private var budget: Float = 0.toFloat()
    private var total_spent: Float = 0.toFloat()
    private var over_under: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedpreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)

        //pull data from internal storage
        budget = sharedpreferences.getFloat(BUDGET, 0.toFloat())
        over_under = sharedpreferences.getFloat(OVER_UNDER, 0.toFloat())
        total_spent = sharedpreferences.getFloat(TOTAL, 0.toFloat())

        val logIn = sharedpreferences.getBoolean(LOGIN, false)
        if(logIn) {
            startActivity(Intent(this, LogInActivity::class.java)
                .putExtra("password", sharedpreferences.getString("password", null)))
        }

        //assignments
        current_budget_value = findViewById(R.id.current_budget_value)
        update_budget = findViewById(R.id.update_budget)
        total_spending_value = findViewById(R.id.total_spending_value)
        add_expense = findViewById(R.id.add_expense)
        over_under_text = findViewById(R.id.over_under)
        details = findViewById(R.id.show_expenses)

        //set texts to TextViews to
        current_budget_value.text = budget.format(2)
        over_under_text.text = over_under.format(2)
        total_spending_value.text = total_spent.format(2)
        overUnderCalculation()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean{
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.info -> {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setMessage("This application is written by:\n" +
                        "Aavash Thapa \nRitik Sharma \nAustin Han").setCancelable(true)
                val alert = dialogBuilder.create()
                alert.setTitle("Student Expense Tracker")
                alert.show()
                true
            }
            R.id.setting -> {
                startActivity(Intent(this, PinSettingActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun update_button(v: View) {
        updateBudgetDialog = UpdateBudgetFragment.newInstance()
        updateBudgetDialog.show(supportFragmentManager, "Budget")
    }

    fun add_button(v: View) {
        addExpenseDialog = AddExpenseFragment.newInstance()
        addExpenseDialog.show(supportFragmentManager, "Add Expense")
    }

    fun delete_button(v: View) {
        deleteExpenseDialog = DeleteExpenseFragment.newInstance()
        deleteExpenseDialog.show(supportFragmentManager, "Delete Expense")
    }

    fun clear_button(v: View) {
        if (getFileStreamPath(FILE_NAME).exists()) {
            total_spending_value.text = (0.toFloat()).format(2)
            total_spent = 0.toFloat()
            sharedpreferences.edit().putFloat(TOTAL, 0.toFloat()).apply()
            //data.clear()
            getFileStreamPath(FILE_NAME).delete()
            overUnderCalculation()
            Toast.makeText(this,
                "Data Cleared!",
                Toast.LENGTH_LONG).show()
        }
        else {
            Toast.makeText(this,
                "File Doesn't Exist!",
                Toast.LENGTH_LONG).show()
        }
    }

    fun details_button(v: View) {
        Log.i(TAG, "startActivity for DetailsActivity")
        val intent = Intent(this, DetailsActivity::class.java)
        intent.putExtra("key", FILE_NAME)
        startActivity(intent)
    }

    override fun applyBudget(x: String?) {
        budget = x!!.toFloat()
        current_budget_value.text = budget.format(2)
        sharedpreferences.edit().putFloat(BUDGET, budget).apply()
        overUnderCalculation()
    }

    override fun applySpending(category: String?, price: String?) {
        if (category != null && price != null) {
            //data[category] = price.toFloat()
            val spent = price.toFloat()
            total_spent += spent
            total_spending_value.text = total_spent.format(2)
            sharedpreferences.edit().putFloat(TOTAL, total_spent).apply()

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val formatted = currentDate.format(formatter)

            val text = "$category, -${spent.format(2)}, $formatted"

            if(!getFileStreamPath(FILE_NAME).exists()){
                try{
                    write(FILE_NAME, text)
                }catch (e: FileNotFoundException){
                    Log.i("MainActivity", "FileNotFoundException")
                }
            }else{
                try {
                    val newText = text+"\n"
                    getFileStreamPath(FILE_NAME).appendText(newText)
                    Log.i("ApplySpending" ,"Append success")
                } catch (e: IOException) {
                    Log.i("ApplySpending", "Append fail")
                }
            }
            overUnderCalculation()
        }
    }

    override fun applyDeleting(category: String?, price: String?) {
        if (category != null && price != null) {
            var flag = 0
            val refinedPrice = price.toFloat().format(2)
            val fis = openFileInput(FILE_NAME)
            val lines = BufferedReader(InputStreamReader(fis)).readLines() as ArrayList<String>

            var currLine : String

            for(line in lines) {
                val x = line.split(Regex(", "), 3)
                if (x[0] == category && x[1] == "-".plus(refinedPrice)) { //found line to delete
                    flag = 1
                    val spent = price.toFloat()
                    total_spent -= spent
                    total_spending_value.text = total_spent.format(2)
                    sharedpreferences.edit().putFloat(TOTAL, total_spent).apply()
                    overUnderCalculation()

                    //don't add this line to new file
                    currLine = ""
                    val newText = currLine+"\n"
                    if(!getFileStreamPath(TEMP_FILE_NAME).exists()){
                        try{
                            write(TEMP_FILE_NAME, newText)
                        }catch (e: FileNotFoundException){
                            Log.i("MainActivity", "FileNotFoundException")
                        }
                    }else{
                        try {
                            getFileStreamPath(TEMP_FILE_NAME).appendText(newText)
                        } catch (e: IOException) {
                            Log.i("ApplyDeleting", "Append fail")
                        }
                    }
                }
                //add every other line to new file
                currLine = line
                val newText = currLine+"\n"
                if(!getFileStreamPath(TEMP_FILE_NAME).exists()){
                    try{
                        write(TEMP_FILE_NAME, newText)
                    }catch (e: FileNotFoundException){
                        Log.i("MainActivity", "FileNotFoundException")
                    }
                }else{
                    try {
                        getFileStreamPath(TEMP_FILE_NAME).appendText(newText)
                    } catch (e: IOException) {
                        Log.i("ApplyDeleting", "Append fail")
                    }
                }

            }
            //overwrite contents of new file into old file and delete new file
            getFileStreamPath(TEMP_FILE_NAME).copyTo(getFileStreamPath(FILE_NAME), true)
            getFileStreamPath(TEMP_FILE_NAME).delete()


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

    @Throws(FileNotFoundException::class)
    private fun write(fileName: String, text: String){

        val fos = openFileOutput(fileName, Context.MODE_PRIVATE)
        val pw = PrintWriter(BufferedWriter(OutputStreamWriter(fos)))

        pw.println(text)

        pw.close()


        Log.i("Write", "File creation success")
    }

    fun overUnderCalculation() {
        if (budget > total_spent) {
            over_under = budget - total_spent
            over_under_text.setTextColor(Color.parseColor("#006400"))

            val text = "UNDER $" + over_under.format(2) + " of the current Budget!"

            over_under_text.text = text
        }
        else if (budget < total_spent) {
            over_under = total_spent - budget
            over_under_text.setTextColor(Color.RED)

            val text = "OVER $" + over_under.format(2) + " of the current Budget!"

            over_under_text.text = text
        }
        else { //budget == total_spent
            over_under = 0.toFloat()
            over_under_text.setTextColor(Color.parseColor("#006400"))
            over_under_text.text = "You are at your current Budget!"
        }

        sharedpreferences.edit().putFloat(OVER_UNDER, over_under).apply()
    }

    //function to format a float to a typical dollar notation
    private fun Float.format(digits: Int): String {
        return "%.${digits}f".format(this)
    }

    companion object{
        private const val myPreference = "mypref"
        private const val BUDGET = "budget"
        private const val OVER_UNDER = "over"
        private const val TOTAL = "total_spent"
        private const val FILE_NAME = "SpendingList.txt"
        private const val TEMP_FILE_NAME = "temp.txt"
        private const val LOGIN = "userLoginPreference"
        private val TAG = "Expense-Tracker"
    }

}



