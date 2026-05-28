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

## 📐 Architecture & Data Flow

The application strictly follows the official Android Architecture Guidelines, implementing the **MVVM (Model-View-ViewModel)** pattern combined with a Repository pattern for clean separation of concerns.

📐 Architecture & System Design

The application is a native Android app built using the modern **MVVM (Model-View-ViewModel)** architectural pattern recommended by Google, ensuring unidirectional data flow and clean separation of concerns.

```mermaid
graph TD
    View["📺 FRONTEND (UI Layer)<br>Jetpack Compose Screens<br>(Home, AirQuality, Map, Ranking)"]
    VM["⚙️ VIEWMODEL LAYER<br>State & Business Logic<br>(MainViewModel / WeatherViewModel)"]
    Repo["📦 REPOSITORY LAYER<br>Single Source of Truth<br>(Handles Cache & Data Sync)"]
    Remote["🌐 REMOTE DATA SOURCE<br>Retrofit API Client<br>(WeatherAPI, OpenWeatherMap, IQAir, WAQI)"]
    Local["💾 LOCAL DATA SOURCE<br>SharedPreferences<br>(Offline Mode Cache)"]

    View -->|Observes UI State via Flows| VM
    VM -->|Requests clean data models| Repo
    Repo -->|Network Requests| Remote
    Repo -->|Fallback Storage| Local

    classDef ui fill:#4285F4,stroke:#333,stroke-width:2px,color:#fff;
    classDef vm fill:#7F52FF,stroke:#333,stroke-width:2px,color:#fff;
    classDef repo fill:#3DDC84,stroke:#333,stroke-width:2px,color:#fff;
    classDef data fill:#5f6368,stroke:#333,stroke-width:2px,color:#fff;

    class View ui;
    class VM vm;
    class Repo repo;
    class Remote,Local data;


### Architectural Modules:

1. **View Layer (UI):** Built entirely with Jetpack Compose. It is completely passive, state-driven, and responds instantly to updates emitted by the ViewModel.
2. **ViewModel Layer:** Acts as a bridge between the UI and the Data layer. It retains state during configuration changes and uses Kotlin Flows to stream clean UI states to the views.
3. **Repository Layer:** Encapsulates the logic for data operations. It decides whether to fetch fresh environmental statistics from the network or fallback to locally cached data.
4. **Data Sources:** * **Remote:** Retrofit interfaces for WeatherAPI, OpenWeatherMap, IQAir, and WAQI.
   * **Local:** SharedPreferences acting as a lightweight persistent cache for offline mode.

---

## 💻 Getting Started

To run this project locally, follow these steps:

1. **Clone the repository:**
   git clone [https://github.com/SpyrKel02/Environmental-App.git](https://github.com/SpyrKel02/Environmental-App.git)

---

## 🔒 Security Note & API Keys

In accordance with security best practices, all **API Keys** used by the application have been excluded from this public repository via the `.gitignore` file and are managed locally inside `local.properties`.
