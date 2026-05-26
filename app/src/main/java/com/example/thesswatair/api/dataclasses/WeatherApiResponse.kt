package com.example.thesswatair.api.dataclasses

data class WeatherApiResponse(
    val current: WeatherCurrent
)
data class WeatherCurrent(
    val temp_c: Double,
    val humidity: Int,
    val wind_kph: Double,
    val air_quality: Map<String, Double>?
) {
    val aqi: Int get(){
        val pm25 = air_quality?.get("pm2_5") ?: 0.0
        return pm25ToAQI(pm25)
    }
}
fun pm25ToAQI(pm25: Double): Int {
    return when {
        pm25 <= 12.0 -> ((50 - 0) / (12.0 - 0.0) * (pm25 - 0.0) + 0).toInt()
        pm25 <= 35.4 -> ((100 - 51) / (35.4 - 12.1) * (pm25 - 12.1) + 51).toInt()
        pm25 <= 55.4 -> ((150 - 101) / (55.4 - 35.5) * (pm25 - 35.5) + 101).toInt()
        pm25 <= 150.4 -> ((200 - 151) / (150.4 - 55.5) * (pm25 - 55.5) + 151).toInt()
        else -> 201
    }
}