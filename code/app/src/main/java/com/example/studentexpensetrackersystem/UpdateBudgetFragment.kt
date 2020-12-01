package com.example.studentexpensetrackersystem

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.DialogFragment


class UpdateBudgetFragment : DialogFragment() {

    private lateinit var updateBudget: EditText
    private lateinit var listener: UpdateBudgetListener

    companion object {

        fun newInstance(): UpdateBudgetFragment {
            return UpdateBudgetFragment()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val view:View = inflater.inflate(R.layout.update_dialog, null)
        updateBudget = view.findViewById(R.id.edit_budget)

        builder.setView(view)
            .setTitle("Update Budget")
            .setNegativeButton("cancel",
                DialogInterface.OnClickListener { dialogInterface, i -> })
            .setPositiveButton("update",
                DialogInterface.OnClickListener { dialogInterface, i ->
                    val budget = updateBudget!!.text.toString().toFloatOrNull()
                    if(budget != null) {
                        listener.applyBudget(budget.toString())
                    }else{
                        Toast.makeText(requireActivity(),
                                "Please enter a valid value",
                                    Toast.LENGTH_SHORT).show()
                    }
                })

        return builder.create()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = try {
            context as UpdateBudgetListener
        } catch (e: ClassCastException) {
            throw ClassCastException(
                context.toString() +
                        "must implement UpdateBudgetListener"
            )
        }
    }

        interface UpdateBudgetListener {
            fun applyBudget(budget: String?)
        }
    }