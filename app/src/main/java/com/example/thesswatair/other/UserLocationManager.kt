package com.example.thesswatair.other

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import kotlinx.coroutines.tasks.await

class UserLocationManager(private val context: Context){
    private val getFusedLocation = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getLocation(): Location? {
        return try{
            getFusedLocation.lastLocation.await()
        }catch(e: Exception){
            null
        }
    }

    fun checkAndEnableGPS(activity: Activity){
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY,1000).build()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client: SettingsClient = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())
        task.addOnFailureListener {
            exception -> if(exception is ResolvableApiException){
                try {
                    exception.startResolutionForResult(activity,1001)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}