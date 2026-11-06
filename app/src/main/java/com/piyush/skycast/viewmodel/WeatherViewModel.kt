// The ViewModel survives configuration changes (like screen rotations)
// so your app doesn't refetch data just because the user rotated their phone.
// using StateFlow, which is the modern Compose alternative to LiveData.

package com.piyush.skycast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.piyush.skycast.network.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel : ViewModel() {
    private val repository = WeatherRepository()

    // Backing property to avoid exposing mutable state to the UI
    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    // We will start with default coordinates (Bengaluru) until we add search/location
    fun fetchWeather(lat: Double = 12.9716, long: Double = 77.5946) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                val response = repository.getWeatherData(lat, long)
                _weatherState.value = WeatherUiState.Success(response)
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error("Network error: ${e.message}")
            }
        }
    }
    fun searchCityWeather(cityName: String) {
        viewModelScope.launch {
            _weatherState.value = WeatherUiState.Loading
            try {
                // Step 1: Get coordinates for the city
                val geoResponse = repository.searchCity(cityName)
                val location = geoResponse.results?.firstOrNull()

                if (location != null) {
                    // Step 2: Fetch weather using those exact coordinates
                    val weatherResponse = repository.getWeatherData(location.latitude, location.longitude)
                    _weatherState.value = WeatherUiState.Success(weatherResponse)
                } else {
                    // Handled gracefully! No city found.
                    _weatherState.value = WeatherUiState.Error("City not found. Please try another name! 🏙️")
                }
            } catch (e: Exception) {
                _weatherState.value = WeatherUiState.Error("Network error: ${e.message}")
            }
        }
    }
}