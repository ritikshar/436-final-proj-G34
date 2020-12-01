package com.example.studentexpensetrackersystem

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import java.io.*

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

    private var data = mutableMapOf<String, Float>()

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

    fun details_button(v: View) {
        //should start new activity of all the map data as a List View
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
            overUnderCalculation()
        }
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
    }
}



