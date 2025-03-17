package com.mobilepiscine42.advanced_weather_app.pageviewer.helpers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mobilepiscine42.advanced_weather_app.R
import com.mobilepiscine42.advanced_weather_app.api.Daily
import com.mobilepiscine42.advanced_weather_app.api.DailyUnits

class WeeklyForecastAdapter(
    private val context: Context,
    private val dailyForecast: Daily,
    private val dailyUnits: DailyUnits
) : RecyclerView.Adapter<WeeklyForecastAdapter.WeeklyForecastViewHolder> () {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeeklyForecastViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_daily_forecast, parent, false)
        return WeeklyForecastViewHolder(view)
    }

    override fun getItemCount(): Int {
        return dailyForecast.time.size
    }

    override fun onBindViewHolder(holder: WeeklyForecastViewHolder, position: Int) {
        val date = Util.formatDate(dailyForecast.time[position])
        val weatherCode = dailyForecast.weather_code[position]
        val maxTemperature = dailyForecast.temperature_2m_max[position]
        val minTemperature = dailyForecast.temperature_2m_min[position]
        holder.bind(date, weatherCode, maxTemperature, minTemperature, dailyUnits)
    }

    inner class WeeklyForecastViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val date : TextView = itemView.findViewById(R.id.date)
        private val weatherIcon: ImageView = itemView.findViewById(R.id.weatherIcon)
        private val maxTemperature: TextView  = itemView.findViewById(R.id.maxTemperature)
        private val minTemperature: TextView = itemView.findViewById(R.id.minTemperature)

        fun bind(date: String, weatherCode: Int, maxTemperature: Double, minTemperature: Double, units: DailyUnits) {
            this.date.text = date
            weatherIcon.setImageDrawable(Util.setWeatherImage(context, weatherCode))
            this.maxTemperature.text = context.getString(R.string.temperature_text, maxTemperature.toString(), units.temperature_2m_max)
            this.minTemperature.text = context.getString(R.string.temperature_text, minTemperature.toString(), units.temperature_2m_min)
        }
    }
}