package com.example.thesswatair

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.thesswatair.api.RetrofitInstance

class FireWorkManager(context: Context,params: WorkerParameters): CoroutineWorker(appContext=context,params){
    override suspend fun doWork(): Result{

        val sharedPref = applicationContext.getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
        val lat = sharedPref.getFloat("last_lat",40.67f).toDouble()
        val lon = sharedPref.getFloat("last_lon",22.94f).toDouble()

        return try{
            val api = RetrofitInstance.api
            val fireResponse = api.getFireInfo(lat,lon, BuildConfig.OPEN_WEATHER_API_KEY)

            val fireTemp = fireResponse?.main?.temp ?:0.0
            val fireHum = fireResponse?.main?.humidity ?:0
            val fireWind = fireResponse?.wind?.speed ?:0.0

            val calcFWI = (fireTemp/40.0) * (fireWind/20.0) * (1-fireHum/100.0) * 100

            if(calcFWI>38.0){
                NotificationHelper.showNotification(context=applicationContext,
                    title="Warning: High Risk for Fire!",
                    message = "The Fire Danger Index is ${String.format("%.1f",calcFWI)}. Be careful!")
            }
            Result.success()
        }catch(e:Exception){
            Result.retry()
        }
    }
}