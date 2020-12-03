package com.example.studentexpensetrackersystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import java.io.File
import java.io.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : AppCompatActivity(), UpdateBudgetFragment.UpdateBudgetListener, AddExpenseFragment.UpdateSpendingListener {

    private lateinit var current_budget_value: TextView
    private lateinit var update_budget: Button
    private lateinit var total_spending_value: TextView
    private lateinit var add_expense: Button
    private lateinit var over_under_text: TextView
    private lateinit var details: Button
    private lateinit var updateBudgetDialog: DialogFragment
    private lateinit var addExpenseDialog: DialogFragment
    private lateinit var sharedpreferences: SharedPreferences
    private lateinit var file: File


    private var data = mutableMapOf<String, Float>()

    private var budget: Float = 0.toFloat()
    private var total_spent: Float = 0.toFloat()
    private var over_under: Float = 0.toFloat()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sharedpreferences = getSharedPreferences(myPreference, Context.MODE_PRIVATE)
        file = File(FILE_NAME)

        //pull data from internal storage
        budget = sharedpreferences.getFloat(BUDGET, 0.toFloat())
        over_under = sharedpreferences.getFloat(OVER_UNDER, 0.toFloat())
        total_spent = sharedpreferences.getFloat(TOTAL, 0.toFloat())

        startActivity(Intent(this, LogInActivity::class.java).putExtra("password", "1234"))

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

    fun update_button(v: View) {
        updateBudgetDialog = UpdateBudgetFragment.newInstance()
        updateBudgetDialog.show(supportFragmentManager, "Budget")
    }

    fun add_button(v: View) {
        addExpenseDialog = AddExpenseFragment.newInstance()
        addExpenseDialog.show(supportFragmentManager, "Add Expense")
    }

    fun delete_button(v: View) {
        //Should create a new dialog similar to add_expense to delete an entry

        /* CODE to delete a line from a file to be used somewhere later
        @Throws(IOException::class)
        fun removeLine(br: BufferedReader, f: File, Line: String) {
            val temp = File("temp.txt")
            val bw = BufferedWriter(FileWriter(temp))
            var currentLine: String
            while (br.readLine().also { currentLine = it } != null) {
                val trimmedLine = currentLine.trim { it <= ' ' }
                if (trimmedLine == Line) {
                    currentLine = ""
                }
            bw.write(currentLine + System.getProperty("line.separator"))
        }
        bw.close()
        br.close()
        val delete = f.delete()
        val b = temp.renameTo(f)
        }
        */
    }

    fun clear_button(v: View) {
        //No creation of a dialog or activity,
        //simply clear the internal data and show a TOAST when done
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
            data[category] = price.toFloat()
            total_spent += price.toFloat()
            total_spending_value.text = total_spent.format(2)
            sharedpreferences.edit().putFloat(TOTAL, total_spent).apply()

            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
            val formatted = currentDate.format(formatter)

            if(!getFileStreamPath(FILE_NAME).exists()){
                try{
                    val text = "$category, ${total_spending_value.text}, $formatted"
                    write(text)
                }catch (e: FileNotFoundException){
                    Log.i("MainActivity", "FileNotFoundException")
                }
            }

            overUnderCalculation()
        }
    }

    @Throws(FileNotFoundException::class)
    private fun write(text: String){
        val fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE)
        val pw = PrintWriter(BufferedWriter(OutputStreamWriter(fos)))

        pw.println(text)

        pw.close()
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
        private val TAG = "Expense-Tracker"
    }
}



