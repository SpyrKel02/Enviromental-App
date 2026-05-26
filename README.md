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

## 🛠️ Architecture & Tech Stack

The application is built using modern Android development best practices and cutting-edge technologies:

* **Language:** Kotlin
* **Architectural Pattern:** MVVM (Model-View-ViewModel) ensuring a strict Separation of Concerns.
* **UI Framework:** Jetpack Compose (Declarative UI) utilizing Unidirectional Data Flow (UDF) and State-driven Recomposition.
* **Asynchronous Programming:** Kotlin Coroutines & Flow for smooth, non-blocking background operations.
* **Local Storage:** SharedPreferences (Key-Value pairs) for lightweight Data Persistence and Offline Mode.
* **Background Scheduling:** WorkManager API for guaranteed execution of persistent background tasks under system constraints.
* **Networking:** Retrofit for REST API interaction and JSON parsing.

---

## 🔒 Security Note & API Keys

In accordance with security best practices, all **API Keys** used by the application have been excluded from this public repository via the `.gitignore` file and are managed locally inside `local.properties`.
