package com.piyush.skycast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
    // This tracks the text typed in the search bar
    var searchQuery by remember { mutableStateOf("") }

    // This observes the UI state from the ViewModel
    val weatherState by viewModel.weatherState.collectAsState()

    // Fetches default weather (Bengaluru) when the app first opens
    LaunchedEffect(Unit) {
        viewModel.fetchWeather()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The Search Bar
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
                    Text("🔍") // Using an emoji for a quick icon, no extra imports needed!
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // The Weather Data Display
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = weatherState) {
                is WeatherUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.padding(top = 50.dp))
                }
                is WeatherUiState.Success -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Current Weather",
                            style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}