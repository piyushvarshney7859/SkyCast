// This class will do the actual job of fetching the data from the API.

package com.piyush.skycast.network

class WeatherRepository {
    suspend fun getWeatherData(lat: Double, long: Double): WeatherResponse {
        return RetrofitClient.weatherApi.getWeather(lat, long)
    }
    // New function to call the Geocoding API
    suspend fun searchCity(name: String): GeocodingResponse {
        return RetrofitClient.weatherApi.searchCity(name)
    }
}