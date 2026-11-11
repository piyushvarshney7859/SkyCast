# SkyCast 🌤️

SkyCast is a modern, location-aware weather application built entirely with Jetpack Compose and Kotlin. It fetches real-time weather data using the Open-Meteo API and dynamically updates UI elements based on current WMO weather codes. 

## Features
* **Live Location Weather:** Automatically fetches coordinates using GPS permissions to show the user's local weather on launch.
* **City Search:** Allows users to search for any city globally to fetch instantaneous weather updates.
* **Dynamic Weather Icons:** Translates raw WMO weather codes into intuitive, dynamic visual emojis (e.g., ☀️ for clear, 🌧️ for rain).
* **Robust Error Handling:** Gracefully manages network drops and invalid city searches.
* **Clean Architecture:** Fully decoupled MVVM architecture ensuring scalable and testable code.

## Tech Stack
* **Language:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM (Model-View-ViewModel)
* **Networking:** Retrofit & Kotlin Coroutines
* **API:** Open-Meteo (Free weather API)

## Getting Started
1. Clone this repository: `git clone https://github.com/piyushvarshney7859/SkyCast.git`
2. Open the project in Android Studio.
3. Build and run on a physical device or emulator.
