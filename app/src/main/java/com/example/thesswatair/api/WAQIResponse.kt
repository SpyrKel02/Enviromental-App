package com.example.thesswatair.api

data class WAQIResponse(
    val status: String,
    val data: List<WaqiStation>
)
data class WaqiStation(
    val lat: Double,
    val lon: Double,
    val aqi: String,
    val station: WaqiStationInfo,
    val uid: Int
)
data class WaqiStationInfo(
    val name: String,
    val time : String?=null
)
data class CityRankingData(
    val city: String,
    val country: String,
    val pollution: RankingPollution
)
data class RankingPollution(
    val aqius: Int
)