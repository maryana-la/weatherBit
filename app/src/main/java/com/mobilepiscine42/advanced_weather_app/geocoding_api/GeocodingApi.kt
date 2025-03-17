package com.mobilepiscine42.advanced_weather_app.geocoding_api

import com.mobilepiscine42.advanced_weather_app.api.Constant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GeocodingApi {
    @GET(Constant.WEATHER_API_SEARCH)
    suspend fun getLocationList (
        @Query("name") city : String,
        @Query("count") count : Int,
    ) : Response<GeoOption>
}