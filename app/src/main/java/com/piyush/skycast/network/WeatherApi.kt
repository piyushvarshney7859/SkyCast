package com.piyush.skycast.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") long: Double,
        @Query("current_weather") currentWeather: Boolean = true
    ): WeatherResponse
    // New endpoint for searching city coordinates
    @GET("https://geocoding-api.open-meteo.com/v1/search")
    suspend fun searchCity(
        @Query("name") name: String,
        @Query("count") count: Int = 1
    ): GeocodingResponse
}