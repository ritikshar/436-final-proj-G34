package com.example.studentexpensetrackersystem

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class MainActivity : AppCompatActivity(), UpdateBudgetFragment.UpdateBudgetListener, AddExpenseFragment.UpdateSpendingListener {

    private lateinit var current_budget_value: TextView
    private lateinit var update_budget: Button
    private lateinit var total_spending_value: TextView
    private lateinit var add_expense: Button
    private lateinit var over_under_text: TextView
    private lateinit var details: Button
    private lateinit var updateBudgetDialog: DialogFragment
    private lateinit var addExpenseDialog: DialogFragment

    private var data = mutableMapOf<String, Int>()

    private var budget = 0
    private var total_spent = 0
    private var over_under = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //startActivity(Intent(this, LogInActivity::class.java).putExtra("password", "1234"))

        current_budget_value = findViewById(R.id.current_budget_value)
        update_budget = findViewById(R.id.update_budget)
        total_spending_value = findViewById(R.id.total_spending_value)
        add_expense = findViewById(R.id.add_expense)
        over_under_text = findViewById(R.id.over_under)
        details = findViewById(R.id.show_expenses)

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
        current_budget_value.text = x
        budget = Integer.parseInt(x)
        overUnderCalculation()
    }

    override fun applySpending(category: String?, price: String?) {
        if (category != null && price != null) {
            data[category] = Integer.parseInt(price)
            total_spent += Integer.parseInt(price)
            total_spending_value.text = total_spent.toString()
            overUnderCalculation()
        }
    }

    fun overUnderCalculation() {
        if (budget > total_spent) {
            over_under = budget - total_spent
            over_under_text.setTextColor(Color.parseColor("#006400"))
            over_under_text.text = "UNDER $" + over_under + " of the current Budget!"
        }
        else if (budget < total_spent) {
            over_under = total_spent - budget
            over_under_text.setTextColor(Color.RED)
            over_under_text.text = "OVER $" + over_under + " of the current Budget!"
        }
        else { //budget == total_spent
            over_under = 0
            over_under_text.setTextColor(Color.parseColor("#006400"))
            over_under_text.text = "You are at your current Budget! "
        }
    }
}