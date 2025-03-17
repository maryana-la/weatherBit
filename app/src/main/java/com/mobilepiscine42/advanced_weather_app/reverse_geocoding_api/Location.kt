package com.mobilepiscine42.advanced_weather_app.reverse_geocoding_api

data class Location(
    val spatialReference: SpatialReference,
    val x: Double,
    val y: Double
)