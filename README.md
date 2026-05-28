![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
# Environmental App 🌍💨

**Environmental App** is a modern and comprehensive Android application designed to operate as a digital observatory for environmental and meteorological conditions. The application aims to inform and protect citizens by consolidating multiple data sources and calculating complex hazard indices in real time.

---

## 🚀 Key Features

* **Comprehensive Environmental Overview:** Simultaneous data retrieval from 4 different environmental and weather APIs (WeatherAPI, OpenWeatherMap, IQAir, WAQI).
* **Real-Time Scientific Indices:** On-the-fly calculations at the Model layer for critical safety metrics:
  * **FWI (Fire Weather Index):** Wildfire risk assessment.
  * **Heat Index:** Thermal stress and discomfort levels.
  * **RRI (Respiratory Risk Index):** A custom, composite respiratory risk index combining air pollutant concentrations with meteorological multipliers.
* **Interactive Map (Google Maps SDK):** Visualization of measurement stations and dynamic generation of safety buffer zones (1km radius) around the user's location.
* **Global Ranking & Search:** Dedicated World Ranking and Station Search screens for comparative air quality benchmarking on a global scale.
* **Background Processing (WorkManager):** Autonomous periodic background checks every 15 minutes, triggering **Push Notifications** when predefined hazard thresholds are breached.
* **Offline Functionality (Caching):** Robust local data persistence mechanism using SharedPreferences to ensure seamless access to critical data without an internet connection.

---
## 📸 Screenshots

| Dashboard | Map | Ranking | Searching
| :-: | :-: |
| <img src="<img width="368" height="766" alt="Screenshot 2026-05-28 182034" src="https://github.com/user-attachments/assets/21cf3a59-335c-424e-93a8-08cf24655d11" />
" width="260"> |
---
## 🛠️ Tech Stack

The application is built using modern Android development best practices and cutting-edge technologies:

* **Language:** Kotlin
* **Architectural Pattern:** MVVM (Model-View-ViewModel) ensuring a strict Separation of Concerns.
* **UI Framework:** Jetpack Compose (Declarative UI) utilizing Unidirectional Data Flow (UDF) and State-driven Recomposition.
* **Asynchronous Programming:** Kotlin Coroutines & Flow for smooth, non-blocking background operations.
* **Local Storage:** SharedPreferences (Key-Value pairs) for lightweight Data Persistence and Offline Mode.
* **Background Scheduling:** WorkManager API for guaranteed execution of persistent background tasks under system constraints.
* **Networking:** Retrofit for REST API interaction and JSON parsing.

---

## 📁 Project Structure

The codebase is organized into modular packages based on feature and architectural responsibility, tracking the core components of the application:

```text
app/src/main/java/com/example/thesswatair/
├── api/                              # Networking & API Configurations
│   ├── dataclasses/                  # API Data Transfer Objects (DTOs)
│   │   ├── IQAirResponse.kt
│   │   ├── OpenWeatherFireResponse.kt
│   │   ├── WAQIResponse.kt
│   │   └── WeatherApiResponse.kt
│   ├── interfaceForAPIs/             # Retrofit endpoints definitions
│   │   └── interfaceForAPIs.kt
│   └── retrofitInstance/             # Retrofit network client initialization
│       └── RetrofitInstance.kt
│
├── navigation/                       # Navigation Graph & Side Menu Routing
│   ├── Menu.kt
│   ├── MenuItems.kt
│   └── NavigationHost.kt             # Compose NavHost component configuration
│
├── notifications/                    # Background Services & Alert Dispatches
│   ├── FireWorkManager.kt            # WorkManager scheduler for periodic safety evaluations
│   └── NotificationHelper.kt         # System notification builders and channels
│
├── other/                            # Business Logic, Calculators & Utility Managers
│   ├── CitiesList.kt
│   ├── EnvironmentalCalculator.kt    # Algorithms computing real-time FWI, RRI, and Heat Index
│   ├── EnvironmentInfo.kt
│   ├── HealthAdvice.kt
│   ├── OfflineCache.kt               # SharedPreferences wrapper handling data caching
│   ├── SelectedAreaInfo.kt
│   ├── ThessalonikiAreas.kt
│   └── UserLocationManager.kt        # Location services wrapper (GPS location fetching)
│
├── screens/                          # Frontend Presentation Layer (Jetpack Compose Views)
│   ├── DashboardScreen.kt            # Application home layout and dashboard metrics
│   ├── MapScreen.kt                  # Google Maps integration with 1km station zone layers
│   ├── RankingScreen.kt              # Global air pollution benchmarking screen
│   ├── Screen.kt                     # Sealed class containing application screen pathways
│   └── SearchScreen.kt               # Interactive query engine looking up specific data
│
├── ui.theme/                         # App Design System styling (Colors, Typography, Themes)
│
├── viewmodel/                        # Architecture Layer StateHolders
│   └── AirQualityViewModel.kt        # Handles presentation state pipelines via Flows
│
└── MainActivity.kt                   # Core application runtime entry point
```
---

## 💻 Getting Started

To run this project locally, follow these steps:

1. **Clone the repository:**
   git clone [https://github.com/SpyrKel02/Environmental-App.git](https://github.com/SpyrKel02/Environmental-App.git)

---

## 🔒 Security Note & API Keys

In accordance with security best practices, all **API Keys** used by the application have been excluded from this public repository via the `.gitignore` file and are managed locally inside `local.properties`.
