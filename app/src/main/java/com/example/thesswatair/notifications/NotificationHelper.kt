package com.example.thesswatair.notifications

import android.Manifest
import android.R
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    fun showNotification(context: Context, title:String, message:String){
        val channelID="fire_alerts_channel_v2"//id του καναλιού
        val channelName="Fire Risk Alerts"//το όνομα που βλέπει ο χρήστης στις ρυθμίσεις
        val notificationManager=context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        //δημιουργία καναλιού
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel=
                NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        //έλεγχος άδειας για Android 13+
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(context,
                    Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED){
                if(context is Activity){
                    ActivityCompat.requestPermissions(context,arrayOf(Manifest.permission.POST_NOTIFICATIONS),1001)
                }
                return
            }
        }
        val notification= NotificationCompat
            .Builder(context,channelID)
            .setContentTitle(title)//τίτλος ειδοποίησης
            .setContentText(message)//το κείμενο
            .setAutoCancel(true)//να κλείνει όταν ο χρήστης την πατάει
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setPriority(NotificationCompat.PRIORITY_HIGH)//υψηλή προτεραιότητα
            .build()

        //εμφάνιση ειδοποίησης με Random ID
        notificationManager.notify(1001,notification)
    }
}