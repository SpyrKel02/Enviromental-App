package com.example.thesswatair.screens

import androidx.compose.runtime.Composable
import android.content.Context
import android.location.Geocoder
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.util.Locale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.VerticalDivider
import androidx.compose.ui.Alignment
import com.example.thesswatair.other.HealthAdvice
import com.example.thesswatair.api.dataclasses.IQWeather
import com.example.thesswatair.viewmodel.AirQViewModel
import com.example.thesswatair.other.EnvironmentInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(airViewModel: AirQViewModel) {
    val context = LocalContext.current

    val displayAQI = airViewModel.getAQI()
    val displayTemp = airViewModel.getTemp()
    val displayHum = airViewModel.getHum()
    val displayWind = airViewModel.getWind()

    val isLoading = airViewModel.isLoading

    val healthAdviceData = EnvironmentInfo.AqiInfo.getHealthAdvice(displayAQI)//function συμβουλών υγείας σχετικά με το AQI
    val aqiColor = EnvironmentInfo.AqiInfo.getAQIColor(displayAQI)//χρώμα container για το health advice

    val calcFWI = airViewModel.getFWI() //δείκτη πυρκαγιάς
    val FWIPercentage = (calcFWI / 50.0 * 100).coerceIn(0.0,100.0)

    val calcRRI = airViewModel.getRRI() //δείκτης αναπνευστικού κινδύνου
    val calcHeatIndex = airViewModel.getHeatIndex() //δείκτης δυσφορίας

    val isFromCache = airViewModel.cachedData != null && !airViewModel.getLoadedData()

    val userLocation = when {
        airViewModel.getLoadedData() -> airViewModel.getCityName(context, airViewModel.lastLat, airViewModel.lastLon)
        isFromCache -> "${airViewModel.cachedData?.city} (Αποθηκευμένο)"
        else -> "Εντοπισμός"
    }
    val time by produceState(initialValue = "", key1 = airViewModel.isLoading) {
        while (true) {
            if (isFromCache) {
                val currentTime = airViewModel.getLastUpdatedTime()
                value = "Τελευταία ενημέρωση: ${currentTime}"
                break
            } else {
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                value = "Τώρα: ${currentTime}"
                delay(60000)
            }
        }
    }
    //μέσω αυτού το box επιτρέπεται το swipe down
    PullToRefreshBox(
        modifier=Modifier.fillMaxSize(),
        isRefreshing = isLoading,
        onRefresh = {
            airViewModel.refreshData()
        }
    ){
        val cardColor = if (isFromCache) Color.Gray else MaterialTheme.colorScheme.primary
        Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)){
            WeatherIndexCard(
                userLocation=userLocation,
                time=time,
                airQuality=displayAQI,
                temp=displayTemp,
                hum=displayHum,
                wind=displayWind,
                containerColor=cardColor
            )//κάρτα σχετικά με τον καιρό, το AQI κτλ
            HealthAdviceIndexCard(healthAdviceData=healthAdviceData, aqiColor=aqiColor)//κάρτα συμβουλών υγείας
            FireDangerIndexCard(calcFWI=calcFWI,FWIPercentage=FWIPercentage)//κάρτα κινδύνου πυρκαγιάς
            HeatIndexAndRRICard(calcHeatIndex=calcHeatIndex ,calcRRI=calcRRI)
        }
    }
}

@Composable
fun WeatherIndexCard(
    userLocation: String,
    time: String,
    airQuality: Int,
    temp: String,
    hum: String,
    wind: String,
    containerColor: Color
){
    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            // Τοποθεσία και Ώρα
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = userLocation, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = time, color = Color.White.copy(alpha = 0.8f))
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            // AQI Section
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("Ποιότητα Αέρα", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(text = airQuality.toString(), fontSize = 42.sp, fontWeight = FontWeight.Black, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Επίπεδο", fontWeight = FontWeight.Bold, color = Color.White)
                    Text(
                        text = EnvironmentInfo.AqiInfo.getAQILevel(airQuality),
                        color = EnvironmentInfo.AqiInfo.getAQIColor(airQuality),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                WeatherDetailItem("Θερμοκρασία", "$temp")
                WeatherDetailItem("Υγρασία", "$hum%")
                WeatherDetailItem("Ταχύτητα ανέμου", "$wind m/s")
            }
        }
    }
}

@Composable
fun WeatherDetailItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, fontSize = 12.sp, color = Color.White.copy(alpha = 0.7f))
        Text(value, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HealthAdviceIndexCard(
    healthAdviceData: HealthAdvice,
    aqiColor: Color
){
    Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp),colors= CardDefaults.cardColors(containerColor = aqiColor.copy(alpha=0.15f))){
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically){
            Text(text=healthAdviceData.icon, fontSize = 45.sp)
            Spacer(modifier=Modifier.width(16.dp))
            Column{
                Text(text=healthAdviceData.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,color=aqiColor)
                Text(text=healthAdviceData.description, fontSize = 14.sp, lineHeight = 18.sp,color= MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun FireDangerIndexCard(
    calcFWI: Double,
    FWIPercentage: Double
){
    Card(shape=RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))){
        Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
            Column{
                Text(text="Δείκτης Κινδύνου Πυρκαγιάς", fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Text(text=EnvironmentInfo.fireInfo.getFireRiskLevel(calcFWI)+": "+"${String.format("%.1f",FWIPercentage)}%",color=EnvironmentInfo.fireInfo.getFireRiskLevelColor(calcFWI), fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(text="FWI: ${String.format("%.1f",calcFWI)}",color=Color.White.copy(alpha = 0.5f), fontSize = 14.sp)//νούμερο με ένα δεκαδικό ψηφίο
            }
        }
    }
}
@Composable
fun HeatIndexAndRRICard(
    calcHeatIndex: Double,
    calcRRI: Double
){
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp) , colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))){
        Row(modifier = Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically){
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "Αίσθηση", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "${String.format("%.1f", calcHeatIndex)}°C", fontSize = 24.sp , fontWeight = FontWeight.Bold, color = EnvironmentInfo.AdvancedIndices.getHeatIndexColor(calcHeatIndex))
                Text(text = if(calcHeatIndex > 30 )"Έντονη ζέστη" else "Άνετα", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
            VerticalDivider(modifier = Modifier.height(50.dp), thickness = 1.dp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f))
            Column(horizontalAlignment = Alignment.CenterHorizontally){
                Text(text = "Αναπνευστικός Κίνδυνος", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text="${String.format("%.0f",calcRRI)}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = if(calcRRI>100) Color.Red else Color.Green)
                Text(text = EnvironmentInfo.AdvancedIndices.getRRILevel(calcRRI), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
            }
        }
    }
}