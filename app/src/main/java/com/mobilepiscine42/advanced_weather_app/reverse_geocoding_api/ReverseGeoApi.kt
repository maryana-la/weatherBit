package com.mobilepiscine42.advanced_weather_app.reverse_geocoding_api

import com.mobilepiscine42.advanced_weather_app.api.Constant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

public interface ReverseGeoApi {
    @GET(Constant.REVERSE_GEO_ENDPOINT)
    suspend fun getLocationName (
        @Query("location") location : String,
        @Query("langCode") langCode : String,
        @Query("forStorage") forStorage : String,
        @Query("f") format : String
    ) : Response<ReverseGeo>
}
