package com.mobilepiscine42.advanced_weather_app.pageviewer

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mobilepiscine42.advanced_weather_app.R
import com.mobilepiscine42.advanced_weather_app.pageviewer.helpers.Util


class Currently : Fragment() {
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_currently, container, false)
        val mainLayout = view?.findViewById<LinearLayout>(R.id.mainLayout)
        val city = view?.findViewById<TextView>(R.id.city)
        val region = view?.findViewById<TextView>(R.id.region)
        val temperature = view?.findViewById<TextView>(R.id.temperature)
        val weatherDescription = view?.findViewById<TextView>(R.id.weatherDescription)
        val weatherIcon = view?.findViewById<ImageView>(R.id.weatherIcon)
        val wind = view?.findViewById<TextView>(R.id.wind)
        val errorMessage = TextView(context)
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

            val temperatureValue = sharedViewModel.getWeatherForecast().current.temperature_2m.toString()
            val temperatureUnit = sharedViewModel.getWeatherForecast().current_units.temperature_2m
            temperature?.text = getString(R.string.temperature_text, temperatureValue, temperatureUnit)

            weatherDescription?.text = Util.setWeatherDescription(sharedViewModel.getWeatherForecast().current.weather_code)
            weatherIcon?.setImageDrawable(context?.let {
                it1 -> Util.setWeatherImage(it1, sharedViewModel.getWeatherForecast().current.weather_code) })

            val windSpeed = sharedViewModel.getWeatherForecast().current.wind_speed_10m.toString()
            val windUnit = sharedViewModel.getWeatherForecast().current_units.wind_speed_10m
            val windDirection = Util.getWindDirection(sharedViewModel.getWeatherForecast().current.wind_direction_10m)
            wind?.text = getString(R.string.wind_text, windSpeed, windUnit, windDirection)
            val windIcon = context?.let { it1 -> ContextCompat.getDrawable(it1, R.drawable.ic_weather_wind) }
            wind?.setCompoundDrawablesWithIntrinsicBounds(windIcon, null, null, null)
        }

        sharedViewModel.errorLiveData.observe(viewLifecycleOwner) {
            if (sharedViewModel.getErrorMsg().isNotEmpty()) {
                Log.e("FRAGMENT Currently", "Error message print")
                city?.text = ""
                region?.text = ""
                temperature?.text = ""
                weatherDescription?.text = ""
                weatherIcon?.setImageDrawable(null)
                wind?.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                wind?.text = ""
                mainLayout?.removeView(errorMessage)
                if (mainLayout != null) {
                    errorMessage.text = sharedViewModel.getErrorMsg()
                    Util.setupErrorMessage(errorMessage, mainLayout)
                }
            }
        }
        return view
    }
}