package com.mobilepiscine42.advanced_weather_app.pageviewer.helpers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.icu.text.SimpleDateFormat
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.TEXT_ALIGNMENT_CENTER
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mobilepiscine42.advanced_weather_app.R
import com.mobilepiscine42.advanced_weather_app.api.Constant
import com.mobilepiscine42.advanced_weather_app.pageviewer.SharedViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale


class Util {

    companion object {

        fun getWindDirection(angle: Int): String {
            val directions = listOf("↓", "↙", "←", "↖", "↑", "↗", "→", "↘")
            return directions[(Math.round(angle.toDouble() / 45) % 8).toInt()]
        }

        fun formatTimeHHMM(input: String): String {
            val dateTime = LocalDateTime.parse(input)
            val formatter = DateTimeFormatter.ofPattern("HH:mm")
            return dateTime.format(formatter)
        }

        fun formatDate(input: String): String {
            return try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val outputFormat = SimpleDateFormat("dd/MM", Locale.getDefault())
                val date = inputFormat.parse(input)
                date?.let { outputFormat.format(it) } ?: ""
            } catch (e: Exception) {
                Log.e("FormatDate Exception", e.toString())
                ""
            }
        }

        fun setupErrorMessage(errorMessage: TextView, mainLayout: LinearLayout) {
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER
                leftMargin = 10
                rightMargin = 10
            }

            errorMessage.layoutParams = layoutParams
            errorMessage.apply {
                setTextColor(Color.RED)
                textAlignment = TEXT_ALIGNMENT_CENTER
                textSize = 25f
                maxLines = 3
                ellipsize = TextUtils.TruncateAt.END
                setLineSpacing(4f, 1.2f)
                visibility = View.VISIBLE
            }
            mainLayout.post {
                if (errorMessage.parent == null) {
                    Log.i("mainLayout?.post", errorMessage.text.toString())
                    mainLayout.addView(errorMessage, 2)
                } else {
                    Log.i("Error msg parent", errorMessage.parent.toString())
                }
            }
        }

        fun removeErrorMessage (errorMessage: TextView) {
            errorMessage.text = ""
            errorMessage.visibility = View.GONE
        }

        fun setWeatherDescription(code: Int): String = when (code) {
            0 -> "Clear sky"
            1, 2, 3 -> "Partly cloudy"
            45, 48 -> "Fog"
            51 -> "Light drizzle"
            53 -> "Moderate drizzle"
            55 -> "Intense drizzle"
            56 -> "Light freezing drizzle"
            57 -> "Intense freezing drizzle"
            61 -> "Slight rain"
            63 -> "Moderate rain"
            65 -> "Heavy rain"
            66, 67 -> "Freezing rain"
            71, 73, 75 -> "Snow fall"
            77 -> "Snow grains"
            80, 81, 82 -> "Rain showers"
            85, 86 -> "Snow showers"
            95 -> "Thunderstorm"
            96, 99 -> "Thunderstorm with hail"
            else -> ""
        }

        fun setWeatherImage(context: Context, weatherCode: Int): Drawable? {
            val drawableResId = when (weatherCode) {
                0 -> R.drawable.ic_weather_sunny
                1, 2, 3 -> R.drawable.ic_weather_partly_cloudy
                45, 48 -> R.drawable.ic_weather_fog
                51, 53, 55, 56, 57 -> R.drawable.ic_weather_drizzle
                61, 63 -> R.drawable.ic_weather_light_rain
                65, 66, 67, 80, 81, 82 -> R.drawable.ic_weather_heavy_rain
                71, 73, 75, 77, 85, 86 -> R.drawable.ic_weather_snow
                95 -> R.drawable.ic_weather_thunderstorm
                96, 99 -> R.drawable.ic_weather_hail
                else -> R.drawable.ic_weather_undefined
            }

            return ContextCompat.getDrawable(context, drawableResId)
        }

        fun resizeVectorDrawable(context: Context, drawableId: Int, width: Int, height: Int): BitmapDrawable {
            val drawable = ContextCompat.getDrawable(context, drawableId) ?: return BitmapDrawable()

            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)

            return BitmapDrawable(context.resources, bitmap)
        }

        fun createChartForHourlyForecast(sharedViewModel: SharedViewModel, lineChart: LineChart) {
            val entries = ArrayList<Entry>()
            val hourlyTemp = sharedViewModel.getWeatherForecast().hourly
            for (i in 0 until Constant.QUANTITY_HOURS_FOR_TODAY_FRAGMENT) {
                entries.add(Entry(i.toFloat(), hourlyTemp.temperature_2m[i].toFloat()))
            }

            val dataSet = LineDataSet(entries, "Temperature")
            designDataSet(dataSet,Color.rgb(16,236,159), Color.WHITE)

            lineChart.data = LineData(dataSet)
            setLineChartAxis(lineChart, TimeAxisValueFormatter())
            lineChart.legend?.isEnabled = false
            lineChart.setExtraOffsets(0f, 0f, 5f, 10f)
        }


        fun createChartForDailyForecast(sharedViewModel: SharedViewModel, lineChart: LineChart) {
            val dateLabels = ArrayList<String>()
            val daily = sharedViewModel.getWeatherForecast().daily

            val minTemperatureEntry = ArrayList<Entry>()
            val maxTemperatureEntry = ArrayList<Entry>()
            for(i in 0 until Constant.FORECAST_DAYS) {
                minTemperatureEntry.add(Entry(i.toFloat(), daily.temperature_2m_min[i].toFloat()))
                maxTemperatureEntry.add(Entry(i.toFloat(), daily.temperature_2m_max[i].toFloat()))
                dateLabels.add(formatDate(daily.time[i]))
            }
            setLineChartAxis(lineChart, IndexAxisValueFormatter(dateLabels))

            val minDataSet = LineDataSet(minTemperatureEntry, "Min Temperature")
            designDataSet(minDataSet,Color.rgb(33,150,243), Color.WHITE)

            val maxDataSet = LineDataSet(maxTemperatureEntry, "Max Temperature")
            designDataSet(maxDataSet, Color.RED, Color.WHITE)

            lineChart.data = LineData(minDataSet, maxDataSet)
            lineChart.xAxis.granularity = 1f
            lineChart.xAxis.setLabelCount(dateLabels.size, true)
            lineChart.setExtraOffsets(0f, 0f, 5f, 15f)

            lineChart.legend.apply {
                textColor = Color.WHITE
                textSize = 12f
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            }
        }

        private fun setLineChartAxis(lineChart: LineChart, formatter: ValueFormatter) {
            lineChart.xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = Color.WHITE
                valueFormatter = formatter
                setDrawGridLines(true)
            }

            lineChart.apply {
                axisLeft.textColor = Color.WHITE
                axisLeft.setDrawGridLines(true)
                axisRight?.isEnabled = false
                description.isEnabled = false
                visibility = View.VISIBLE
                setTouchEnabled(true)
                setPinchZoom(true)
                animateX(1800, Easing.EaseInExpo)
            }
            lineChart.invalidate()
        }

        private fun designDataSet(dataSet: LineDataSet, lineColor: Int, circleColor: Int) {
            dataSet.apply {
                setDrawValues(false)
                setDrawFilled(false)
                setDrawCircles(true)
                setCircleColor(lineColor)
                circleRadius = 5f
                circleHoleColor = circleColor
                color = lineColor
                valueTextSize = 10f
                lineWidth = 2f
            }
        }
    }
}