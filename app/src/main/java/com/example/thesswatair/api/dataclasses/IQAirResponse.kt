package com.example.thesswatair.api.dataclasses
data class IQAirResponse(
    val status:String,
    val data: AirData
)
data class AirData(
    val city:String,
    val state: String,
    val country:String,
    val current:CurrentData,
    val location:LocationData
)
data class CurrentData(
    val pollution:Pollution,
    val weather:IQWeather
)
data class LocationData(
    val coordinates:List<Double>
)
data class Pollution(
    val aqius:Int//δείκτης AQI
)
data class IQWeather(
    val tp:Int,//θερμοκρασία
    val hu:Int,//υγρασία
    val ws:Double,//ταχύτητα ανέμου
    val ic: String//εικονίδιο καιρού
)