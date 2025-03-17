package com.mobilepiscine42.advanced_weather_app.geocoding_api

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mobilepiscine42.advanced_weather_app.api.Constant
import com.mobilepiscine42.advanced_weather_app.pageviewer.SharedViewModel
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class GeocodingViewModel : ViewModel() {

    private val geocodingApi = RetrofitInstance.geocodingApi

    fun getData(city : String, sharedViewModel: SharedViewModel) {
        viewModelScope.launch {
            try {
                val response = geocodingApi.getLocationList(city.trim(), Constant.NUMBER_OF_SEARCH_RESULT)
                if (response.body()?.results != null) {
                    Log.i("Success", response.body().toString())
                    sharedViewModel.setCityOptions(response.body()?.results ?: emptyList())
                } else {
                    Log.i("Error geocoding viewmodel", response.message())
                    sharedViewModel.setCityOptions(emptyList())
                }
            } catch (e : IOException) {
                Log.e("Network", "No internet connection", e)
                sharedViewModel.setErrorMsg("No internet connection.\nPlease check your network and try again.")
            } catch (e : HttpException) {
                Log.e ("API error", "HTTP: ${e.message()}", e)
                sharedViewModel.setErrorMsg("Error fetching weather data.\nPlease try again later.")
            } catch (e : Exception) {
                Log.e ("WeatherViewModel", "Unexpected error: ${e.message}", e)
                sharedViewModel.setErrorMsg("Unexpected error has happened.\nPlease try again later.")
            }
        }
    }
}