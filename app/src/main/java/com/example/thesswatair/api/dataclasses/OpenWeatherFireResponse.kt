package com.example.thesswatair.api.dataclasses

data class OpenWeatherFireResponse(
    val main: MainFireData,
    val wind: WindFireData,
    val name: String
)
data class MainFireData(
    val temp: Double,
    val humidity: Int
)
data class WindFireData(
    val speed: Double
)