package com.mobilepiscine42.advanced_weather_app.pageviewer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mobilepiscine42.advanced_weather_app.api.WeatherModel
import com.mobilepiscine42.advanced_weather_app.geocoding_api.Result
import com.mobilepiscine42.advanced_weather_app.reverse_geocoding_api.Address


class SharedViewModel : ViewModel() {

    val cityOptionsLiveData: LiveData<List<Result>> get() = cityOptions
    private var cityOptions = MutableLiveData<List<Result>>()

    val forecastLiveData: LiveData<WeatherModel> get() = weatherForecast
    private var weatherForecast = MutableLiveData<WeatherModel>()

    val cityLiveData: LiveData<Address> get() = currentCity
    private var currentCity = MutableLiveData<Address>()

    val errorLiveData: LiveData<String> get() = errorMsg
    private var errorMsg = MutableLiveData<String>()

    fun setCityOptions(fromAPI : List<Result>) {
        cityOptions.value = fromAPI
    }

    fun getCityOptions() : List<Result> {
        return cityOptions.value ?: emptyList()
    }

    fun setWeatherForecast(forecast : WeatherModel) {
        weatherForecast.value = forecast
    }

    fun getWeatherForecast() : WeatherModel {
        return weatherForecast.value!!
    }

    fun setCurrentCity (city : Address) {
        currentCity.value = city
    }

    fun getCurrentCity() : Address {
        return currentCity.value!!
    }

    fun setErrorMsg(message : String) {
        errorMsg.postValue(message)
    }

    fun getErrorMsg() : String {
        return errorMsg.value!!
    }
}