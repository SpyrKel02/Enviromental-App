package com.example.thesswatair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.thesswatair.ui.theme.ThessWatAirTheme
import androidx.lifecycle.lifecycleScope
import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import kotlinx.coroutines.launch
import androidx.compose.runtime.LaunchedEffect
import com.example.thesswatair.menu.Menu
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlin.getValue
import com.example.thesswatair.viewmodel.AirQViewModel
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

       val constraints = androidx.work.Constraints.Builder()
           .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)
           .build() // λέμε στην εφαρμογή να τρέξει την ειδοποίηση για πυρκαγία μόνο όταν έχει ίντερνετ

        val workRequest = PeriodicWorkRequestBuilder<FireWorkManager>(2, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FireRiskCheck",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )

        val airViewModel: AirQViewModel by viewModels()
        // Δημιουργούμε τον manager τοποθεσίας
        val locationManager = UserLocationManager(this)

        setContent {
            ThessWatAirTheme {
                val permissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestMultiplePermissions()
                ) { permissions ->
                    // Αν ο χρήστης πατήσει "Ναι" στην άδεια
                    if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                        // Ελέγχουμε αν το GPS είναι ανοιχτό
                        locationManager.checkAndEnableGPS(this@MainActivity)

                        lifecycleScope.launch {
                            val loc = locationManager.getLocation()
                            loc?.let {
                                airViewModel.loadAirQuality(it.latitude, it.longitude)
                                airViewModel.loadFireRisk(it.latitude,it.longitude)

                                //αποθηκεύονται οι συντεταγμένες για τον WorkManager
                                val sharedPrefs=getSharedPreferences("location_prefs", Context.MODE_PRIVATE)
                                with(sharedPrefs.edit()){
                                    putFloat("last_lat",it.latitude.toFloat())
                                    putFloat("last_lon",it.longitude.toFloat())
                                    apply()
                                }
                            }
                        }
                    }
                }

                LaunchedEffect(Unit) {
                    // Ζητάμε την άδεια τοποθεσίας μόλις ανοίξει η εφαρμογή
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                    // Ελέγχουμε και το GPS αμέσως
                    locationManager.checkAndEnableGPS(this@MainActivity)
                }

                Menu(airViewModel)
            }
        }
    }
}