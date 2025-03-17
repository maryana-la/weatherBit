package com.mobilepiscine42.advanced_weather_app.pageviewer.helpers

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class TimeAxisValueFormatter : IndexAxisValueFormatter() {

    override fun getFormattedValue(value : Float) : String {
        val hours = value.toInt()
        val minutes = ((value - hours) * 60).toInt()
        return String.format("%02d:%02d", hours, minutes)
    }
}