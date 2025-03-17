package com.mobilepiscine42.advanced_weather_app.geocoding_api

import com.mobilepiscine42.advanced_weather_app.api.Constant
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private fun getInstance() : Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constant.GEOCODING_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val geocodingApi : GeocodingApi = getInstance().create(GeocodingApi::class.java)
}