package com.example.studentexpensetrackersystem

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment

class AddExpenseFragment: DialogFragment() {
    private lateinit var category: EditText
    private lateinit var price: EditText
    private lateinit var listener: UpdateSpendingListener

    companion object {

        fun newInstance(): AddExpenseFragment {
            return AddExpenseFragment()
        }
        private val TAG = "Expense-Tracker"
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view: View = inflater.inflate(R.layout.add_expense, null)
        category = view.findViewById(R.id.edit_category)
        price = view.findViewById(R.id.edit_price)

        builder.setView(view)
            .setTitle("Add Expense")
            .setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialogInterface, i -> })
            .setPositiveButton("add",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val category = category!!.text.toString()
                    val price = price!!.text.toString().toFloatOrNull()
                    if(price != null) {
                        listener.applySpending(category, price.toString())
                    }
                    else{
                        Toast.makeText(requireActivity(),
                                "Enter a valid value",
                                Toast.LENGTH_SHORT).show()
                        Log.i(TAG, "Price has to be a number")
                    }
                })

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as UpdateSpendingListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement UpdateBudgetListener"
            )
        }
    }

    interface UpdateSpendingListener {
        fun applySpending(category: String?, price: String?)
    }

}