package com.example.thesswatair

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.thesswatair.ui.theme.ThessWatAirTheme
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.work.*
import java.util.concurrent.TimeUnit
import kotlin.getValue
import com.example.thesswatair.viewmodel.AirQViewModel
import com.example.thesswatair.navigation.Menu
import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.example.thesswatair.notifications.FireWorkManager
import com.example.thesswatair.other.UserLocationManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Fire Risk Alerts"
            val descriptionText = "Notifications for high fire risk levels"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("fire_risk_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        setupFireRiskAlert()

        val airViewModel: AirQViewModel by viewModels()
        // Δημιουργούμε τον manager τοποθεσίας
        val locationManager = UserLocationManager(this)
        setContent {
            ThessWatAirTheme {
                LocationPermissionFunction(airViewModel,locationManager,this)
                Menu(airViewModel)
            }
        }
    }
    private fun setupFireRiskAlert(){//function για ειδοποίηση πιθανής πυρκαγιάς
        val constraints = androidx.work.Constraints.Builder()//δημιουργία Builder για τον ορισμό των προϋποθέσεων/περιορισμών που πρέπει να ισχύουν
            .setRequiredNetworkType(androidx.work.NetworkType.CONNECTED)//να τρέχει μόνο όταν έχει ενεργή σύνδεση wifi στο κινητό
            .build()

        val workRequest = PeriodicWorkRequestBuilder<FireWorkManager>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()//αίτημα για περιοδική εργασία και να εκτελεί τον κώδικα που βρίσκεται μέσα στην κλάση FireWorkManager ανά 1 ώρα

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "FireAlertJob",//μοναδικό όνομα στην εργασία για να ξέρει το Android ποια είναι
            ExistingPeriodicWorkPolicy.KEEP,//ορίζει να μην ξεκινήσει νέα εργασία από την αρχή αλλά να κρατήσει αυτή που βρίσκεται ήδη στο παρασκήνιο σε περίπτωση που η εφαρμογή ανοίξει ξανά
            workRequest
        )
    }
}
@Composable
private fun LocationPermissionFunction(//function για χορήγηση άδειας της τοποθεσιας του χρήστη
    airViewModel: AirQViewModel,
    locationManager: UserLocationManager,
    activity: Activity
){
    val permissionLauncher = rememberLauncherForActivityResult(//εκκινητής που θα αναλάβει να ζητήσει τις άδειες από το σύστημα του Android και να θυμάται το αποτέλεσμα, χωρίς να χάνεται η κατάσταση κατά το recomposition του UI
        ActivityResultContracts.RequestMultiplePermissions()//ορίζει ότι ο launcher θα ζητήσει πολλαπλές άδειες ταυτόχρονα
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true) {//ελέγχει αν ο χρήστης έδωσε άδεια για ακριβή τοποθεσία
            locationManager.checkAndEnableGPS(activity)//καλεί τον manager για να ελέγξει αν το GPS της συσκευής είναι ανοιχτό και αν είναι κλειστό εμφανίζει το κλασικό παράθυρο του συστήματος για να το ενεργοποιήσει
            airViewModel.refreshData()
        }
    }
    LaunchedEffect(Unit) {//λόγω του Unit ο κώδικας μέσα σε αυτό το μπλοκ εκτελείται μόνο μια φορά κατά την διάρκεια του αρχικού composition δηλαδή την πρώτη φορά που εμφανίζεται η οθόνη στον χρήστη
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,//ακριβής τοποθεσία χρήστη
                Manifest.permission.ACCESS_COARSE_LOCATION//προσεγγιστική τοποθεσία δηλαδή που περίπου βρίσκεται
            )
        )
        // Ελέγχουμε και το GPS αμέσως
        locationManager.checkAndEnableGPS(activity)
    }
}