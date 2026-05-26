package com.example.thesswatair.other

import android.content.Context
import android.util.Log
import android.content.ContentValues.TAG
object OfflineCache{
    private const val prefs_name = "offline_cache"
    fun save(//function όπου σώζουμε στην cache τα δεδομένα που θέλουμε
        context: Context,
        aqi: Int,
        temp: Double,
        hum: Int,
        wind: Double,
        fwi: Double,
        rri: Double,
        heatIndex: Double,
        city: String,
    ){
        val prefs = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)
        prefs.edit().apply{
            putInt("aqi",aqi)
            putFloat("temp",temp.toFloat())
            putInt("hum",hum)
            putFloat("wind",wind.toFloat())
            putFloat("fwi",fwi.toFloat())
            putFloat("rri",rri.toFloat())
            putFloat("heatIndex",heatIndex.toFloat())
            putString("city",city)
            putLong("timestamp", System.currentTimeMillis())
            apply()
        }
    }
    fun load(context: Context): CachedData{
        val prefs = context.getSharedPreferences(prefs_name, Context.MODE_PRIVATE)

        val city = prefs.getString("city", "Unknown") ?: "Unknown"
        val timestamp = prefs.getLong("timestamp", 0L)

        Log.d(TAG, "Loading from Cache: City=$city, Timestamp=$timestamp")
        return CachedData(
            aqi = prefs.getInt("aqi", 0),
            temp = prefs.getFloat("temp", 0f).toDouble(),
            hum = prefs.getInt("hum", 0),
            wind = prefs.getFloat("wind", 0f).toDouble(),
            fwi = prefs.getFloat("fwi", 0f).toDouble(),
            rri = prefs.getFloat("rri", 0f).toDouble(),
            heatIndex = prefs.getFloat("heatIndex",0f).toDouble(),
            city = prefs.getString("city","Unknown") ?: "Unknown",
            timestamp = prefs.getLong("timestamp",0)
        )
    }
}
data class CachedData(
    val aqi: Int,
    val temp: Double,
    val hum: Int,
    val wind: Double,
    val fwi: Double,
    val rri: Double,
    val heatIndex: Double,
    val city: String,
    val timestamp: Long
)