package com.example.studentexpensetrackersystem

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.DialogFragment

/*
RemovePinFragment: Fragment pops up in pin setting to
                    see if you want to remove the pin
                    that was created. The user has the
                    choice either to remove or cancel.
 */
class RemovePinFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val sharedpreferences = activity?.getSharedPreferences("mypref", Context.MODE_PRIVATE)

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Would you like to remove your pin?")
                .setPositiveButton(R.string.remove,
                    DialogInterface.OnClickListener { dialog, id ->
                        sharedpreferences!!.edit()
                            .remove("userLoginPreference").remove("password").apply()

                        dismiss()
                    })
                .setNegativeButton(R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dismiss()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}