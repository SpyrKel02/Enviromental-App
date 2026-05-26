package com.example.thesswatair.notifications

import android.annotation.SuppressLint
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.thesswatair.BuildConfig
import com.example.thesswatair.api.retrofitinstance.RetrofitInstance
import com.example.thesswatair.other.EnvironmentalCalculator

//κλάση που είναι υπεύθυνη για τις offline ειδοποιήσεις
class FireWorkManager(context: Context, params: WorkerParameters): CoroutineWorker(appContext=context,params){
    @SuppressLint("SuspiciousIndentation")
    override suspend fun doWork(): Result{

        val sharedPref = applicationContext.getSharedPreferences("air_prefs", Context.MODE_PRIVATE)
        val lat = sharedPref.getFloat("last_lat",40.67f).toDouble()
        val lon = sharedPref.getFloat("last_lon",22.94f).toDouble()

        return try{
            val api = RetrofitInstance.api
            val fireData = api.getFireInfo(lat,lon, BuildConfig.OPEN_WEATHER_API_KEY)
            val unifiedData = api.getUnifiedData(BuildConfig.WEATHER_API_KEY,"$lat,$lon")

            val temp = fireData?.main?.temp?.toDouble() ?: unifiedData?.current?.temp_c ?: 20.0
            val tempC = if (temp > 100.0) temp - 273.15 else temp

            //2) μετατροπή ανέμου σε km/h
            val wind = when {
                fireData != null -> fireData?.wind?.speed?.toDouble()?.let { it * 3.6 } ?: 0.0 //μετατροπή m/s σε km/h
                unifiedData != null -> unifiedData?.current?.wind_kph?.toDouble() ?: 0.0 //ήδη σε km/h
                else -> 0.0
            }
            val hum = fireData?.main?.humidity?.toDouble() ?: unifiedData?.current?.humidity?.toDouble() ?: 50.0

            val calcFWI = EnvironmentalCalculator.calculateFWI(temp=tempC, wind=wind, hum=hum)
            val FWIPercentage = (calcFWI / 50.0 * 100).coerceIn(0.0,100.0)

            val currentLevel = getFireRiskLevel(calcFWI)
            val lastLevel = sharedPref.getString("last_notified_level", "")

            if(calcFWI>21.3){
                NotificationHelper.showNotification(
                    context = applicationContext,
                    title = "Προειδοποίηση: Κίνδυνος Πυρκαγιάς ($currentLevel)",
                    message = "Ο δείκτης πυρκαγιάς είναι ${String.format("%.1f", FWIPercentage)}%."
                )
                sharedPref.edit().putString("last_notified_level", currentLevel).apply()
            }else {
                sharedPref.edit().putString("last_notified_level","").apply()
            }
            Result.success()
        }catch(e:Exception){
            Result.retry()
        }
    }
    fun getFireRiskLevel(calcFWI:Double):String{
        return when{
            calcFWI < 5.2 -> "Πολύ Χαμηλή Πιθανότητα"
            calcFWI < 11.2 -> "Χαμηλή Πιθανότητα"
            calcFWI < 21.3 -> "Μέτρια Πιθανότητα"
            calcFWI < 38.0 -> "Υψηλή Πιθανότητα"
            calcFWI < 50.0 -> "Πολύ Υψηλή Πιθανότητα"
            calcFWI >= 50.0 -> "Ακραία Πιθανότητα"
            else -> "Άγνωστο"
        }
    }
}