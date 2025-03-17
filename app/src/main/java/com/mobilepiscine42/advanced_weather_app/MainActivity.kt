package com.mobilepiscine42.advanced_weather_app

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.mobilepiscine42.advanced_weather_app.api.Constant.REQUEST_CODE_LOCATION_PERMISSION
import com.mobilepiscine42.advanced_weather_app.api.WeatherViewModel
import com.mobilepiscine42.advanced_weather_app.geocoding_api.GeocodingViewModel
import com.mobilepiscine42.advanced_weather_app.geocoding_api.Result
import com.mobilepiscine42.advanced_weather_app.pageviewer.SharedViewModel
import com.mobilepiscine42.advanced_weather_app.pageviewer.helpers.CitySuggestionAdapter
import com.mobilepiscine42.advanced_weather_app.pageviewer.helpers.ViewPagerAdapter
import com.mobilepiscine42.advanced_weather_app.reverse_geocoding_api.ReverseGeoViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient : FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationManager: LocationManager
    private lateinit var weatherViewModel : WeatherViewModel
    private lateinit var geocodingViewModel : GeocodingViewModel
    private lateinit var reverseGeoViewModel : ReverseGeoViewModel
    private lateinit var sharedViewModel : SharedViewModel
    private lateinit var searchView : SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CitySuggestionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        setPageView()
        setLocationService()
        setSearchView()
        weatherViewModel = ViewModelProvider(this)[WeatherViewModel::class.java]
        geocodingViewModel = ViewModelProvider(this)[GeocodingViewModel::class.java]
        sharedViewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        reverseGeoViewModel = ViewModelProvider(this)[ReverseGeoViewModel::class.java]
        sharedViewModel.cityOptionsLiveData.observe(this) {
            Log.i("Cities", sharedViewModel.getCityOptions().toString())
            adapter.updateSuggestions(sharedViewModel.getCityOptions())
        }
    }

    private fun setPageView() {
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        val viewPager = findViewById<ViewPager2>(R.id.viewPager2)
        val adapter = ViewPagerAdapter(this)
        viewPager.adapter = adapter

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bottomNavigationView.menu.getItem(position).isChecked = true
            }
        })

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.currently -> viewPager.currentItem = 0
                R.id.today -> viewPager.currentItem = 1
                R.id.weekly -> viewPager.currentItem = 2
                else -> viewPager.currentItem = 0
            }
            true
        }
    }

    private fun setLocationService() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 50000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(2000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val latitude = location.latitude
                    val longitude = location.longitude
                    Log.d("Location Update", "Lat: $latitude, Lon: $longitude")
                }
            }
        }
    }

    private fun setSearchView() {
        searchView = findViewById(R.id.searchGeoText)
        searchView.isIconified = false
        searchView.clearFocus()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CitySuggestionAdapter(mutableListOf()) { city -> onCitySelected(city) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    Log.i ("City name : ", query)
                    Log.e ("OnQueryTextSubmit", "${query}, log")
                    geocodingViewModel.getData(query, sharedViewModel)

                    searchView.setQuery("", false)
                    searchView.clearFocus()
                    recyclerView.visibility = View.GONE
                    val tmp = sharedViewModel.getCityOptions()
                    if (tmp.size != 0) {
                       onCitySelected(tmp[0])
                    } else {
                        sharedViewModel.setErrorMsg("Could not find any result for the provided address or coordinates.")
                    }
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText == null || newText.length < 3)  {
                    Log.e ("OnQueryTextChange", "${newText}, log")
                    adapter.updateSuggestions(emptyList())
                    recyclerView.visibility = RecyclerView.GONE
                    return true
                }
                newText.let { query ->
                    if (query.isNotBlank()) {
                        recyclerView.visibility = RecyclerView.VISIBLE
                        geocodingViewModel.getData(query, sharedViewModel)
                    } else {
                        searchView.clearFocus()
                        adapter.updateSuggestions(emptyList())
                        recyclerView.visibility = View.GONE
                    }
                }
                return true
            }
        })

        searchView.setOnCloseListener {
            Log.e("onCLoseListener", "log")
            searchView.clearFocus()
            adapter.updateSuggestions(emptyList())
            recyclerView.visibility = View.GONE
            true
        }
    }

    private fun onCitySelected(city: Result) {
        weatherViewModel.getData(city.latitude.toString(), city.longitude.toString(), sharedViewModel, reverseGeoViewModel)
        searchView.setQuery("", false)
        searchView.clearFocus()
        sharedViewModel.setCityOptions(emptyList())
        recyclerView.visibility = View.GONE
        Log.i("RecycleView","Selected City: ${city.name}")
    }

    fun requestLocation(view: View) {
        if (isLocationPermissionGranted()) {
            getGPS()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION),REQUEST_CODE_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                getGPS()
            } else {
                sharedViewModel.setErrorMsg("Location permission is not granted.\nPlease enable GPS access in the settings.")
                Log.i("Location permission", "Location permission denied")
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getGPS() {
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i("GPS", "GPS is disabled! Please enable it in settings.")
            sharedViewModel.setErrorMsg("GPS is disabled.\nPlease turn it on in settings and try again.")
            return
        }

        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, mainLooper)
        val task: Task<Location> = fusedLocationProviderClient.
        getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, CancellationTokenSource().token)
        searchView.clearFocus()
        task.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude.toString()
                val longitude = location.longitude.toString()
                weatherViewModel.getData(latitude, longitude, sharedViewModel, reverseGeoViewModel)
                Log.d("GPS", "latitude: $latitude, longitude: $longitude")
            } else {
                Log.e("GPS", "Error getting GPS location!")
                sharedViewModel.setErrorMsg("Cannot get location from GPS.\nPlease try again later.")
            }
        }.addOnFailureListener { e ->
            Log.e("Location", "Error fetching location", e)
            sharedViewModel.setErrorMsg("Error getting location from GPS.\nPlease try again later.")
        }
    }

    private fun isLocationPermissionGranted(): Boolean {
        return !(ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
    }

    override fun onDestroy() {
        super.onDestroy()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }

}



