package com.mobilepiscine42.advanced_weather_app.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET(Constant.WEATHER_API_ENDPOINT)
    suspend fun getWeather (
        @Query("latitude") latitude : String,
        @Query("longitude") longitude : String,
        @Query("current") current : String,
        @Query("hourly") hourly : String,
        @Query("daily") daily : String,
        @Query("forecast_days") forecastDays : Int
    ) : Response<WeatherModel>
}