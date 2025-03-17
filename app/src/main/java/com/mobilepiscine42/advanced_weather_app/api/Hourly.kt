package com.mobilepiscine42.advanced_weather_app.api

data class Hourly(
    val temperature_2m: List<Double>,
    val time: List<String>,
    val weather_code: List<Int>,
    val wind_direction_10m: List<Int>,
    val wind_speed_10m: List<Double>
)