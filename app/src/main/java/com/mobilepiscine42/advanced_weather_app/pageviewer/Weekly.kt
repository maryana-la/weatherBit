package com.mobilepiscine42.advanced_weather_app.pageviewer

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.LineChart
import com.mobilepiscine42.advanced_weather_app.R
import com.mobilepiscine42.advanced_weather_app.pageviewer.helpers.Util
import com.mobilepiscine42.advanced_weather_app.pageviewer.helpers.WeeklyForecastAdapter

class Weekly : Fragment() {

    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val screenHeight = resources.displayMetrics.heightPixels
        val view = inflater.inflate(R.layout.fragment_weekly, container, false)
        val mainLayout = view.findViewById<LinearLayout>(R.id.mainLinearLayout)
        val lineChart = view.findViewById<LineChart>(R.id.lineChart)
        val city = view?.findViewById<TextView>(R.id.city)
        val region = view?.findViewById<TextView>(R.id.region)
        val errorMessage = TextView(context)
        val forecastRecyclerView = view?.findViewById<RecyclerView>(R.id.forecastRecyclerView)!!
        forecastRecyclerView.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true) // Stop parent from intercepting touch
            false
        }

        lineChart?.setOnTouchListener { v, _ ->
            v.parent.requestDisallowInterceptTouchEvent(true) // Stop parent from intercepting touch
            false
        }

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        sharedViewModel.cityLiveData.observe(viewLifecycleOwner) {
            mainLayout?.removeView(errorMessage)
            Util.removeErrorMessage(errorMessage)
            city?.text = sharedViewModel.getCurrentCity().City
            region?.text = listOfNotNull(sharedViewModel.getCurrentCity().Region,  sharedViewModel.getCurrentCity().CntryName)
                .joinToString(separator = ", ")
        }

        sharedViewModel.forecastLiveData.observe(viewLifecycleOwner) {
            mainLayout?.removeView(errorMessage)
            Util.removeErrorMessage(errorMessage)

            val dailyForecast = sharedViewModel.getWeatherForecast().daily

            forecastRecyclerView.visibility = View.VISIBLE
            forecastRecyclerView.layoutManager =
                LinearLayoutManager(parentFragment?.context, LinearLayoutManager.HORIZONTAL, false)
            val adapter = context?.let { it1 ->
                WeeklyForecastAdapter(it1, dailyForecast, sharedViewModel.getWeatherForecast().daily_units)
            }
            forecastRecyclerView.adapter = adapter

            if (lineChart != null) {
                lineChart.layoutParams.height = (screenHeight * 0.3).toInt()
                Util.createChartForDailyForecast(sharedViewModel, lineChart)
            }

        }

        sharedViewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (sharedViewModel.getErrorMsg().isNotEmpty()) {
                Log.e("FRAGMENT Weekly", "Error message print")
                city?.text = ""
                region?.text = ""
                mainLayout?.removeView(errorMessage)
                forecastRecyclerView.visibility = View.GONE
                lineChart.visibility = View.GONE
                if (mainLayout != null) {
                    errorMessage.text = sharedViewModel.getErrorMsg()
                    Util.setupErrorMessage(errorMessage, mainLayout)
                }
            }
        }


        return view
    }




}