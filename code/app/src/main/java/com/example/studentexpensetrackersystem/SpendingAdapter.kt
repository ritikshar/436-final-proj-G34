package com.example.studentexpensetrackersystem

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*




class SpendingAdapter(context: Context, spendingArr: ArrayList<String>): BaseAdapter(){

    var mContext = context
    var spendingArr = spendingArr

     override fun getView(
        position: Int,
        convertView: View?,
        parent: ViewGroup
    ): View {

        // Check if the existing view is being reused, otherwise inflate the view
         Log.i(TAG, "getView()")
         Log.i(TAG, "Size of the arrayList: " + count)
        var listItemView :View

        var viewHolder = ViewHolder()
        
    if(convertView == null) {
        listItemView = LayoutInflater.from(mContext).inflate(R.layout.list_details, parent, false)
        viewHolder.expenseView = listItemView.findViewById<View>(R.id.expense_name) as TextView
        viewHolder.expenseView.setTypeface(null, Typeface.BOLD)
        viewHolder.priceView = listItemView.findViewById<View>(R.id.price) as TextView
        viewHolder.priceView.setTypeface(null, Typeface.BOLD)
        viewHolder.dateView = listItemView.findViewById<View>(R.id.date) as TextView
        viewHolder.dateView.setTypeface(null, Typeface.BOLD)
        listItemView.tag = viewHolder

    }
    else{
        listItemView = convertView
    }
        // Return the whole list item layout (containing 2 TextViews and an ImageView)
        // so that it can be shown in the ListView
        val mholder = listItemView.tag as ViewHolder
        val currentItem = getItem(position) as String
        val list = currentItem.split(Regex(", "), 3)
        mholder.expenseView.text = list[0]
         //Log.i(TAG, "Error appears here: " + list[0])
        mholder.priceView.text = list[1]
        mholder.dateView.text = list[2]
        
        return listItemView
    }

    override fun getItem(position: Int): Any {
        return spendingArr[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return spendingArr.size
    }


    fun notifyChange(): Boolean {
        notifyDataSetChanged()
        return true
    }

    internal class ViewHolder {
        lateinit var expenseView: TextView
        lateinit var priceView: TextView
        lateinit var dateView: TextView
    }


    companion object {
        private val TAG = "Expense-Tracker"
    }
}


