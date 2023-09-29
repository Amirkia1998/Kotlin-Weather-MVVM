package com.amirkia.weather.view

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class CityAdapter(context: Context, data: List<Pair<String, String>>) :
    ArrayAdapter<Pair<String, String>>(context, android.R.layout.simple_spinner_item, data) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val cityData = getItem(position)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${cityData?.first}  (${cityData?.second})"

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        val cityData = getItem(position)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = "${cityData?.first}  (${cityData?.second})"

        return view
    }




}
