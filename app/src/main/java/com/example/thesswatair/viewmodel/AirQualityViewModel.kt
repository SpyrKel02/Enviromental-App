package com.example.thesswatair.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.thesswatair.api.RetrofitInstance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.example.thesswatair.api.IQAirResponse
import com.example.thesswatair.api.OpenWeatherFireResponse
import kotlinx.coroutines.launch
import com.example.thesswatair.BuildConfig
import com.example.thesswatair.api.CityRankingData
import com.example.thesswatair.api.RankingPollution
import com.example.thesswatair.api.WaqiStation
import com.example.thesswatair.api.WaqiStationInfo

class AirQViewModel: ViewModel() {
    var airData by mutableStateOf<IQAirResponse?>(null)
    var fireData by mutableStateOf<OpenWeatherFireResponse?>(null)
    private val api = RetrofitInstance.api

    var lastLat by mutableStateOf(0.0)
        private set
    var lastLon by mutableStateOf(0.0)
        private set
    var isLoading by mutableStateOf(false)
        private set

    val calcFWI: Double//υπολογισμός δείκτη πυρκαγιάς
        get() {
            val fireTemp = fireData?.main?.temp ?: 0.0
            val fireWind = fireData?.wind?.speed ?: 0.0
            val fireHum = fireData?.main?.humidity ?: 0
            return (fireTemp / 40.0) * (fireWind / 20.0) * (1 - fireHum / 100.0) * 100
        }

    val calcRRI: Double//δείκτης αναπνευστικού κινδύνου
        get(){
            val aqi = airData?.data?.current?.pollution?.aqius ?:0
            val hum = airData?.data?.current?.weather?.hu ?:0
            return (aqi.toDouble() * 0.7) + (hum.toDouble() * 0.3)
        }

    val calcHeatIndex: Double//πηγή για δείκτη δυσφορίας:https://www.wpc.ncep.noaa.gov/html/heatindex_equation.shtml
        get(){
            val temp = fireData?.main?.temp ?:0.0
            val hum = airData?.data?.current?.weather?.hu ?:0//h=rh στο τύπο της ιστοσελίδας
            if(temp < 20.0){
                return temp
            }
            else{
                return 0.5 * (temp + 61.0 + ((temp - 17.8) * 1.2) + (hum * 0.094))
            }
        }

    val calcPDF: Double//δείκτης που υπολογίζει πόσο εύκολα καθαρίζει ο αέρας μιας περιοχής από τους ρύπους.
        get(){
            val wind = airData?.data?.current?.weather?.ws ?:0.0
            val aqi = airData?.data?.current?.pollution?.aqius ?:0.0
            return (wind + 1.0)/aqi.toDouble()
        }
    fun loadAirQuality(lat: Double, lon: Double) {//φορτώνει την ποιότητα του αέρα (AQI) στην περιοχή που βρίσκεται ο χρήστης
        lastLat = lat
        lastLon = lon
        viewModelScope.launch {
            isLoading = true
            try {
                val response = api.getAirQuality(lat = lat, lon = lon, apikey = BuildConfig.IQAIR_API_KEY)
                airData = response
            }catch(e: retrofit2.HttpException){
                Log.e("API_TEST", "IQAIR ERROR: ${e.code()} - ${e.message()}")
            }catch(e: Exception){
                Log.e("API_TEST", "IQAIR NETWORK ERROR: ${e.message}")
            }finally{
                isLoading=false
            }
        }
    }
    fun refreshData(context:android.content.Context){
        val locationManager=com.example.thesswatair.UserLocationManager(context)
        viewModelScope.launch{
            isLoading=true
            val loc=locationManager.getLocation()
            loc?.let{
                loadAirQuality(it.latitude,it.longitude)
                loadFireRisk(it.latitude,it.longitude)
            }
            isLoading=false
        }
    }

    fun loadFireRisk(lat:Double,lon:Double){// function που φορτώνει τα δεδομένα για τον υπολογισμό της δείκτη πυρκαγιάς στην περιοχή όπου βρίσκεται ο χρήστης
        viewModelScope.launch{
            try {
                val response =
                    api.getFireInfo(lat = lat, lon = lon, apiKey = BuildConfig.OPEN_WEATHER_API_KEY)
                fireData = response
            }catch(e: retrofit2.HttpException){
                Log.e("API_TEST", "OPENWEATHER ERROR: ${e.code()} - ${e.message()}")
            }catch(e:Exception){
                Log.e("API_TEST", "OPENWEATHER NETWORK ERROR: ${e.message}")
            }finally {
                isLoading=false
            }
        }
    }

    val cityRankings = mutableStateListOf<CityRankingData>()
    init {
        loadRankings()
    }
    fun loadRankings(){
        viewModelScope.launch{
            try{
                val response = api.getCityRankings("-90,-180,90,180",BuildConfig.WAQI_API_TOKEN)
                if(response.status=="ok"){ val sortedList = response.data.filter{ it.aqi!="-" && it.aqi.toIntOrNull()!=null }
                        .map{
                            val parts=it.station.name.split(",")
                            CityRankingData(
                                city = parts.first().trim(),
                                country = if(parts.size > 1) parts.last().trim() else "Global",
                                pollution = RankingPollution(aqius = it.aqi.toInt())
                            )
                        }
                        .sortedByDescending { it.pollution.aqius }//κατάταξη από το μεγαλύτερο AQI στο μικρότερο
                        .take(100)//των top 100
                        cityRankings.clear()
                        cityRankings.addAll(sortedList)
                }
            }catch(e: Exception){
                e.printStackTrace()
            }finally {
                isLoading=false
            }
        }
    }

    var citySearchResults = mutableStateListOf<WaqiStation>()
        private set
    fun searchCity(cityName: String){
        val query = cityName.trim()//καθαρισμός κενών
        if(query.isEmpty()){
            return
        }
        viewModelScope.launch{
          try {
              val response = api.searchCity(query, BuildConfig.WAQI_API_TOKEN)
              if(response.status=="ok"){val sortedList = response.data.filter { it.aqi!="-" && it.aqi.toIntOrNull()!=null }
                  .map{station ->
                      station
                  }
                  citySearchResults.clear()
                  citySearchResults.addAll(sortedList)
              }
          }catch(e: Exception){
              Log.e("API_ERROR", "Search failed: ${e.message}")
          }
        }
    }

    var worldMapStations = mutableStateListOf<WaqiStation>()
        private set
    fun loadWorldStations(latLng: String){
        viewModelScope.launch{
            try {
                val response = api.getCityRankings(latLng,BuildConfig.WAQI_API_TOKEN)//μπορεί να χρησιμοποιηθεί και για να πάρουμε όλους τους σταθμούς
                if(response.status=="ok"){
                    val validStations = response.data.filter { it.aqi == "-" }//παίρνουμε τους σταθμούς που το AQI τους δεν είναι κενό
                    worldMapStations.clear()
                    worldMapStations.addAll(validStations)
                }
            }catch(e: Exception){
                Log.e("API_ERROR","Load failed: ${e.message}")
            }
        }
    }
}