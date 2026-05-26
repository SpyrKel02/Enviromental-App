package com.example.thesswatair.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.example.thesswatair.api.retrofitinstance.RetrofitInstance
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.thesswatair.api.dataclasses.IQAirResponse
import com.example.thesswatair.api.dataclasses.OpenWeatherFireResponse
import kotlinx.coroutines.launch
import com.example.thesswatair.BuildConfig
import com.example.thesswatair.api.dataclasses.CityRankingData
import com.example.thesswatair.api.dataclasses.RankingPollution
import com.example.thesswatair.api.dataclasses.WaqiStation
import android.app.Application
import android.content.Context
import android.location.Geocoder
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateMapOf
import com.example.thesswatair.api.dataclasses.WeatherApiResponse
import com.example.thesswatair.other.CachedData
import com.example.thesswatair.other.CitiesList
import com.example.thesswatair.other.EnvironmentalCalculator
import com.example.thesswatair.other.OfflineCache
import com.example.thesswatair.other.ThessalonikiAreas
import com.example.thesswatair.other.UserLocationManager
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AirQViewModel(application: Application): AndroidViewModel(application) {
    var airData by mutableStateOf<IQAirResponse?>(null)
    var fireData by mutableStateOf<OpenWeatherFireResponse?>(null)
    var unifiedData by mutableStateOf<WeatherApiResponse?>(null)
    var cachedData by mutableStateOf<CachedData?>(null)
    private val api = RetrofitInstance.api
    var lastLat by mutableDoubleStateOf(0.0)
    var lastLon by mutableDoubleStateOf(0.0)
    var isLoading by mutableStateOf(false)
        private set
    private val setLoadedData: Boolean
        get() = airData != null || fireData != null || unifiedData != null
    fun getLoadedData(): Boolean{
        return setLoadedData
    }

    init {//μπλοκ κώδικα γα την αρχικοποίηση ενός αντικειμένου το οποίο θα δημιουργείται αυτόματα πχ για κατατάξεις
        cachedData = OfflineCache.load(getApplication())
        viewModelScope.launch{
            loadRankings()
        }
        automatedCheck()
    }

    private val setFWI: Double//υπολογισμός δείκτη πυρκαγιάς
    get() {
        if(fireData!=null || unifiedData!=null){
            //1) μετατροπή kelvin σε celsius αν χρειαστεί
            val temp = fireData?.main?.temp?.toDouble() ?: unifiedData?.current?.temp_c ?: 20.0
            val tempC = if (temp > 100.0) temp - 273.15 else temp

            //2) μετατροπή ανέμου σε km/h
            val wind = when {
                fireData != null -> fireData?.wind?.speed?.toDouble()?.let { it * 3.6 } ?: 0.0 //μετατροπή m/s σε km/h
                unifiedData != null -> unifiedData?.current?.wind_kph?.toDouble() ?: 0.0 //ήδη σε km/h
                else -> 0.0
            }
            val hum = fireData?.main?.humidity?.toDouble() ?: unifiedData?.current?.humidity?.toDouble() ?: 50.0

            val result = EnvironmentalCalculator.calculateFWI(temp = tempC, wind = wind, hum = hum)

            return result
        }
        else{
            return cachedData?.fwi ?: 0.0
        }
    }

    fun getFWI(): Double{
        return setFWI
    }

    private val setRRI: Double
        get() {
        val air = unifiedData?.current?.air_quality

        if (air != null) {
            val tempC = unifiedData?.current?.temp_c
                ?: fireData?.main?.temp?.let { it - 273.15 }
                ?: 20.0

            val hum = unifiedData?.current?.humidity?.toDouble()
                ?: fireData?.main?.humidity?.toDouble()
                ?: 50.0

            return EnvironmentalCalculator.calculateRRI(air, tempC, hum)
        }
        return cachedData?.rri ?: 0.0
        }
    fun getRRI(): Double{
        return setRRI
    }
    private val setHeatIndex: Double
        get() {
            if (unifiedData != null) {
                val tempC = unifiedData?.current?.temp_c ?: 20.0
                val tempK = tempC + 273.15
                val hum = unifiedData?.current?.humidity?.toDouble() ?: 50.0
                return EnvironmentalCalculator.calculateHeatIndex(tempK = tempK, hum = hum)
            }
            if (fireData != null) {
                val tempK = fireData?.main?.temp?.toDouble() ?: 0.0
                val hum = fireData?.main?.humidity?.toDouble() ?: 0.0
                return EnvironmentalCalculator.calculateHeatIndex(tempK = tempK, hum = hum)
            }
            return cachedData?.heatIndex ?: 0.0
        }
    fun getHeatIndex(): Double{
        return setHeatIndex
    }
    private val setAQI: Int
        get() {
            return airData?.data?.current?.pollution?.aqius
                ?: unifiedData?.current?.aqi
                ?: cachedData?.aqi ?:0 //το (?:) δίνει μια ιεραρχία προτεραιότητας σε περίπτωση που δεν βρει σε έστω ένα από αυτά
                                     // δηλαδή πρώτα από το API της IQAir, μετά από το WeatherAPI και τέλος από την cache
        }
    fun getAQI(): Int{
        return setAQI
    }
    private val setTemp: String
        get() {
            return unifiedData?.current?.temp_c?.let { "${it.toInt()}°C" }
                ?: fireData?.main?.temp?.let { "${(it-273.15).toInt()}°C" }
                ?: cachedData?.temp?.let { "${(it-273.15).toInt()}°C" }
                ?: "--"
        }
    fun getTemp(): String{
        return setTemp
    }
    private val setHum: String
        get() {
            return unifiedData?.current?.humidity?.toString()
                ?: fireData?.main?.humidity?.toString()
                ?: cachedData?.hum?.toString()
                ?: "--"
        }
    fun getHum(): String{
        return setHum
    }
    private val setWind: String
        get() {
            return unifiedData?.current?.wind_kph?.let { String.format("%.1f", it) }
                ?:fireData?.wind?.speed?.let { String.format("%.1f", it * 3.6) }
                ?:cachedData?.wind?.let { String.format("%.1f", it * 3.6) }
                ?: "--"
        }
    fun getWind(): String{
        return setWind
    }
    fun refreshData() {
        val locationManager = UserLocationManager(getApplication())
        viewModelScope.launch {
            isLoading = true
            try {
                val loc = locationManager.getLocation()
                if (loc != null) {
                    saveLocationPrefs(loc.latitude, loc.longitude)

                    val airRefresh = async { loadAirQuality(loc.latitude, loc.longitude) }
                    val fireRefresh = async { loadFireRisk(loc.latitude, loc.longitude) }
                    val unifiedRefresh = async { loadUnifiedWeather(loc.latitude, loc.longitude) }

                    awaitAll(airRefresh, fireRefresh, unifiedRefresh)

                    if (getLoadedData()) {
                        val currentCity = getCityName(getApplication(), loc.latitude, loc.longitude)
                        Log.d("VM_DEBUG", "Live data received. Saving to cache for $currentCity")

                        OfflineCache.save(
                            context = getApplication(),
                            aqi = getAQI(),
                            temp = unifiedData?.current?.temp_c?.let { it + 273.15 } ?: fireData?.main?.temp ?: 293.15,
                            hum = unifiedData?.current?.humidity ?: fireData?.main?.humidity?.toInt() ?: 50,
                            wind = unifiedData?.current?.wind_kph?.let { it / 3.6 } ?: fireData?.wind?.speed?.toDouble() ?: 0.0,
                            fwi = getFWI(),
                            rri = getRRI(),
                            heatIndex = getHeatIndex(),
                            city = currentCity
                        )
                        cachedData = null // Καθαρίζουμε το cache state αφού έχουμε φρέσκα δεδομένα
                    } else {
                        cachedData = OfflineCache.load(getApplication())
                    }
                } else {
                    cachedData = OfflineCache.load(getApplication())
                }
            } catch (e: Exception) {
                cachedData = OfflineCache.load(getApplication())
            } finally {
                isLoading = false
            }
        }
    }
    private fun saveLocationPrefs(lat: Double, lon: Double) {//function που αποθηκεύει τις τελευταίες συντεταγμένες του χρήστη
        val sharedPref = getApplication<Application>().getSharedPreferences("air_prefs", Context.MODE_PRIVATE)//άνοιγμα ή δημιουργία ενός αρχείου τοπικής αποθήκευσης και καμία άλλη εφαρμογή δεν μπορεί να την διαβάσει
        with(sharedPref.edit()) {//ανοίγει για να γράψουμε δεδομένα και μέσω του with επιτρέπεται να γράψουμε μαζεμένες εντολές
            putFloat("last_lat", lat.toFloat())
            putFloat("last_lon", lon.toFloat())
            apply() // ασύγρονη αποθήκευση στο παρασκήνιο
        }
        lastLat = lat
        lastLon = lon
    }
    suspend fun loadAirQuality(lat: Double, lon: Double) {//φορτώνει την ποιότητα του αέρα (AQI) στην περιοχή που βρίσκεται ο χρήστης
        try {
            val response = api.getAirQuality(lat = lat, lon = lon, apikey = BuildConfig.IQAIR_API_KEY)
            airData = response
        }catch(e: Exception){
            Log.e("API_TEST", "IQAIR NETWORK ERROR: ${e.message}")
        }
    }
    suspend fun loadFireRisk(lat:Double,lon:Double){// function που φορτώνει τα δεδομένα για τον υπολογισμό της δείκτη πυρκαγιάς στην περιοχή όπου βρίσκεται ο χρήστης
        try {
            val response = api.getFireInfo(lat = lat, lon = lon, apiKey = BuildConfig.OPEN_WEATHER_API_KEY)
            fireData = response
            Log.d("FIRE_DATA", "API Success! Temp: ${response.main.temp}, Hum: ${response.main.humidity}")
        }catch(e:Exception){
            Log.e("FIRE_DATA", "API ERROR: ${e.message}")
        }
    }
    suspend fun loadUnifiedWeather(lat: Double, lon:Double){
        try{
            unifiedData = api.getUnifiedData(BuildConfig.WEATHER_API_KEY,"$lat,$lon")
        }catch (e: Exception){
            unifiedData = null
            Log.e("API_TEST", "WeatherAPI Error: ${e.message}")
        }
    }
    val cityRankings = mutableStateListOf<CityRankingData>()
    private suspend fun loadRankings(){//function για φόρτωση κατάταξης των σταθμών παγκοσμίως, μπαίνει σε init για να γίνεται η αρχικοποίηση ή η δημιουργία μόλις ανοίξει η εφαρμογή
        try{
            isLoading = true
            val response = api.getCityRankings("-90,-180,90,180",BuildConfig.WAQI_API_TOKEN)
            if(response.status=="ok"){
                val sortedList = response.data.asSequence().filter{ it.aqi!="-" && it.aqi.toIntOrNull()!=null }
                    .filter { it->
                        CitiesList.majorCitiesWithCountries.keys.any{ city->
                            it.station.name.contains(city, ignoreCase = true)
                        }
                    }
                    .map{ it->
                        val cityName = CitiesList.majorCitiesWithCountries.keys.first{ city ->
                            it.station.name.contains(city, ignoreCase = true)
                        }
                        val countryName = CitiesList.majorCitiesWithCountries[cityName] ?:"Global"

                        CityRankingData(
                            city = cityName,
                            country = countryName,
                            pollution = RankingPollution(aqius = it.aqi.toInt())
                        )
                    }
                    .groupBy { it.city }
                    .map { group -> group.value.maxBy { it.pollution.aqius } }
                    .sortedByDescending { it.pollution.aqius }//κατάταξη από το μεγαλύτερο AQI στο μικρότερο
                    .take(50)//των top 100
                    .toList()

                cityRankings.clear()
                cityRankings.addAll(sortedList)
            }
        }catch(e: Exception){
            e.printStackTrace()
        }finally {
            isLoading=false
        }
    }
    private fun automatedCheck(){
        viewModelScope.launch {
            while(true){
                refreshData()
                delay(15*60*1000)
            }
        }
    }
    fun getLastUpdatedTime(): String{
        val timestamp = if(getLoadedData()){
            System.currentTimeMillis()
        }else{
            cachedData?.timestamp ?: 0L
        }
        return if (timestamp == 0L){
            "Never"
        }else{
            val time = SimpleDateFormat("HH:mm", Locale.getDefault())
            time.format(Date(timestamp))
        }
    }
    private var jobSearch: Job? = null
    var isSearching by mutableStateOf(false)
    val citySearchResults = mutableStateListOf<WaqiStation>()
    fun searchCity(cityName: String){//function για αναζήτηση πόλης από τον χρήστη
        val query = cityName.trim()//καθαρισμός κενών
        jobSearch?.cancel()

        if(query.length < 2){
            citySearchResults.clear()
            isSearching = false
            return
        }

        isSearching = true

        jobSearch = viewModelScope.launch {
            delay(400)
            try{
                executeCitySearch(query)
            }finally{
                isSearching = false
            }
        }
    }
    private suspend fun executeCitySearch(query: String){
        try {
            val response = api.searchCity(query, BuildConfig.WAQI_API_TOKEN)
            if(response.status=="ok"){
                val sortedList = response.data.filter {
                    it.aqi!="-" && it.aqi.toIntOrNull()!=null
                }
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
    private fun setCityName(context:Context,lat:Double,lon: Double):String{
        if(lat==0.0 && lon==0.0) return "Locating"
        return try{
            val geocoder = Geocoder(context, Locale.getDefault())
            val address = geocoder.getFromLocation(lat,lon,1)
            val city = address?.firstOrNull()?.locality
            val adminArea = address?.firstOrNull()?.adminArea

            city ?: adminArea ?: "Unknown Location"
        }catch(e:Exception){
            "Unknown Location"
        }
    }
    fun getCityName(context: Context, lat: Double, lon: Double): String{
        return setCityName(context, lat, lon)
    }
    var showMode by mutableStateOf(false)// στην MapScreen θα υπάρχουν δύο modes, ένα για το ΑQI(false) και ένα για το FWI(true)
    val areas = ThessalonikiAreas.thessAreas
    val areasState = mutableStateMapOf<String, ThessalonikiAreas.areasData>()
    fun fetchAreasData(){
        viewModelScope.launch(Dispatchers.IO){
            areas.forEach { area ->
                try {
                    val response = api.getUnifiedData(
                        BuildConfig.WEATHER_API_KEY,
                        "${area.coord.latitude},${area.coord.longitude}"
                    )
                    response.let {
                        val aqi = it.current.aqi
                        val fwi = EnvironmentalCalculator.calculateFWI(
                            it.current.temp_c,
                            it.current.wind_kph,
                            it.current.humidity.toDouble()
                        )
                        val rri = EnvironmentalCalculator.calculateRRI(
                            it.current.air_quality,
                            it.current.temp_c,
                            it.current.humidity.toDouble()
                        )
                        val heatIndex = EnvironmentalCalculator.calculateHeatIndex(
                            (it?.current?.temp_c ?: 20.0) + 273.15,
                            it.current.humidity.toDouble()
                        )
                        withContext(Dispatchers.Main){
                            areasState[area.name] = ThessalonikiAreas.areasData(aqi, fwi, rri, heatIndex, isLoading = false)
                        }
                    }
                }catch(e: Exception){
                    withContext(Dispatchers.Main){
                        Log.e("ViewModel", "Error fetching data for ${area.name}: ${e.message}")
                        areasState[area.name] = ThessalonikiAreas.areasData(isLoading = false)
                    }
                }
            }
        }
    }
    fun getDistanceToArea(userLat: Double, userLon: Double, areaCoord: LatLng): Float{
        val results = FloatArray(1)
        android.location.Location.distanceBetween(
            userLat, userLon,
            areaCoord.latitude, areaCoord.longitude,
            results
        )
        return results[0]
    }
}