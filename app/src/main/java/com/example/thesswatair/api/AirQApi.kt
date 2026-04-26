package com.example.thesswatair.api

import retrofit2.http.GET
import retrofit2.http.Query

interface AirQApi {
    //επιστρέφει τα δεδομένα της πλησιέστερης πόλης,χρησιμοποιώντας τη γεωγραφική τοποθεσία της διεύθυνσης IP
    @GET("v2/nearest_city")
    suspend fun getAirQuality(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("key") apikey: String
    ): IQAirResponse

    @GET("https://api.openweathermap.org/data/2.5/weather")
    suspend fun getFireInfo(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): OpenWeatherFireResponse

   //προσθήκη για το ranking των πόλεων από την ιστοσελίδα https://waqi.info/#/c/4.18/7.991/2.2z (χρησιμοποιείται και για την εμφάνιση όλων των σταθμών στο MapScreen)
   @GET("https://api.waqi.info/map/bounds/")
   suspend fun getCityRankings(
       @Query("latlng") latlng: String,
       @Query("token") token: String
   ):WAQIResponse

   @GET("https://api.waqi.info/search/")
   suspend fun searchCity(
       @Query("keyword") keyword: String,
       @Query("token") token: String
   ): WAQIResponse
}