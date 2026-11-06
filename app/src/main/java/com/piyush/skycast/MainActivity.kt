package com.piyush.skycast

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.piyush.skycast.ui.theme.SkyCastTheme
import com.piyush.skycast.viewmodel.WeatherUiState
import com.piyush.skycast.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyCastTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeatherScreen()
                }
            }
        }
    }
}

@Composable
fun WeatherScreen(viewModel: WeatherViewModel = viewModel()) {
    val context = LocalContext.current
    // 1. Initialize the FusedLocationProviderClient
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    var searchQuery by remember { mutableStateOf("") }
    val weatherState by viewModel.weatherState.collectAsState()

    // 2. Create the Permission Launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            val isGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
            if (isGranted) {
                // Fetch exact location if granted
                try {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        if (location != null) {
                            viewModel.fetchWeather(location.latitude, location.longitude)
                        } else {
                            viewModel.fetchWeather() // Fallback to default if GPS is off
                        }
                    }
                } catch (e: SecurityException) {
                    viewModel.fetchWeather()
                }
            } else {
                // Fallback to default if permission denied
                viewModel.fetchWeather()
            }
        }
    )

    // 3. Request Permission when the app opens
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFine || hasCoarse) {
            // Already have permission, get location directly
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        viewModel.fetchWeather(location.latitude, location.longitude)
                    } else {
                        viewModel.fetchWeather()
                    }
                }
            } catch (e: SecurityException) {
                viewModel.fetchWeather()
            }
        } else {
            // Ask for permission! This triggers the popup.
            locationPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // --- UI REMAINS THE SAME ---
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search City") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = {
                    if (searchQuery.isNotBlank()) {
                        viewModel.searchCityWeather(searchQuery.trim())
                    }
                }) {
                    Text("🔍")
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            when (val state = weatherState) {
                is WeatherUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
                is WeatherUiState.Success -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current Weather",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        // This is the new dynamic icon!
                        Text(
                            text = getWeatherIcon(state.weather.currentWeather.weatherCode),
                            style = MaterialTheme.typography.displayLarge,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Text(
                            text = "${state.weather.currentWeather.temperature}°C",
                            style = MaterialTheme.typography.displayLarge
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Wind Speed: ${state.weather.currentWeather.windSpeed} km/h",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                is WeatherUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

fun getWeatherIcon(code: Int): String {
    return when (code) {
        0 -> "☀️" // Clear sky
        1, 2, 3 -> "⛅" // Partly cloudy or overcast
        45, 48 -> "🌫️" // Fog
        51, 53, 55, 56, 57, 61, 63, 65, 66, 67 -> "🌧️" // Drizzle and Rain
        71, 73, 75, 77, 85, 86 -> "❄️" // Snow
        80, 81, 82 -> "🌦️" // Rain showers
        95, 96, 99 -> "⛈️" // Thunderstorm
        else -> "🌡️" // Default
    }
}