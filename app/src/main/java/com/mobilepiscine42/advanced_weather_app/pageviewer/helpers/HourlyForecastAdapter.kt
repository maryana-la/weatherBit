package com.mobilepiscine42.advanced_weather_app.pageviewer.helpers

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobilepiscine42.advanced_weather_app.R
import com.mobilepiscine42.advanced_weather_app.api.Constant
import com.mobilepiscine42.advanced_weather_app.api.Hourly
import com.mobilepiscine42.advanced_weather_app.api.HourlyUnits

class HourlyForecastAdapter(
    private val context: Context,
    private var hourlyForecast: Hourly,
    private val hourlyUnits: HourlyUnits
) : RecyclerView.Adapter<HourlyForecastAdapter.HourlyForecastViewHolder>()  {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hourly_forecast, parent, false)
        return HourlyForecastViewHolder(view)
    }


    override fun onBindViewHolder(holder: HourlyForecastViewHolder, position: Int) {
        val timeStamp = hourlyForecast.time[position]
        val weatherCode = hourlyForecast.weather_code[position]
        val temperature = hourlyForecast.temperature_2m[position]
        val wind = hourlyForecast.wind_speed_10m[position]
        val windDirection = Util.getWindDirection(hourlyForecast.wind_direction_10m[position])
        Log.d("onBindViewHolder position", position.toString())
        holder.bind(timeStamp, weatherCode, temperature, wind, windDirection, hourlyUnits)
    }


    override fun getItemCount(): Int {
        Log.d ("forecast adapter, item count", hourlyForecast.time.size.toString())
        return Constant.QUANTITY_HOURS_FOR_TODAY_FRAGMENT
    }



    inner class HourlyForecastViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val timeStamp: TextView = itemView.findViewById(R.id.timeStamp)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
        private val temperatureHour: TextView = itemView.findViewById(R.id.temperatureHour)
        private val windHour: TextView = itemView.findViewById(R.id.windHour)

        fun bind(time: String, weatherCode: Int, temperature: Double, wind: Double, windDirection: String, units: HourlyUnits) {
            Log.d("ForecastViewHolder: onBind", time)
            timeStamp.text = Util.formatTimeHHMM(time)
            weatherIcon.setImageDrawable(Util.setWeatherImage(context, weatherCode))
            temperatureHour.text = context.getString(R.string.temperature_text, temperature.toString(),units.temperature_2m)
            windHour.text = context.getString(R.string.wind_text, wind.toString(), units.wind_speed_10m, windDirection)
            val windIcon = context.let { it1 ->
                Util.resizeVectorDrawable(
                    it1,
                    R.drawable.ic_weather_wind,
                    40,
                    40
                )
            }
            windHour.setCompoundDrawablesWithIntrinsicBounds(windIcon, null, null, null)
        }
    }
}



