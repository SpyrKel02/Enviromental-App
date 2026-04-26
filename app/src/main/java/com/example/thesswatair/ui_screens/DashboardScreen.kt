package com.example.thesswatair.ui_screens

import androidx.compose.runtime.Composable
import android.content.Context
import android.location.Geocoder
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import com.example.thesswatair.HealthAdvice
import com.example.thesswatair.NotificationHelper
import com.example.thesswatair.viewmodel.AirQViewModel
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(airViewModel: AirQViewModel) {
    val context = LocalContext.current

    val response = airViewModel.airData
    val isLoading = airViewModel.isLoading
    val airData = response?.data
    val airQuality = airData?.current?.pollution?.aqius ?:0
    val weatherData = airData?.current?.weather

    val healthAdviceData = getHealthAdvice(airQuality)//function συμβουλών υγείας σχετικά με το AQI

    val calcFWI = airViewModel.calcFWI //δείκτη πυρκαγιάς
    val calcRRI = airViewModel.calcRRI //δείκτης αναπνευστικού κινδύνου
    val calcHeatIndex = airViewModel.calcHeatIndex //δείκτης δυσφορίας
    val calcPdf = airViewModel.calcPDF //δείκτης που υπολογίζει πόσο εύκολα καθαρίζει ο αέρας μιας περιοχής από τους ρύπους.

    LaunchedEffect(calcFWI){
        if(calcFWI>38.0){
            NotificationHelper.showNotification(context=context,
                title="Warning: High Risk for Fire!",
                message = "The Fire Danger Index is ${String.format("%.1f",calcFWI)}.Be very careful!")
        }
    }

    val userLocation = if(response!=null){
        getCityName(context,airViewModel.lastLat,airViewModel.lastLon)
    }else{
        "Loading..."
    }
    val time by produceState(initialValue = ""){
        while(true){
            value= SimpleDateFormat("HH:mm",Locale.getDefault()).format(Date())
            delay(60000)
        }
    }

    //μέσω αυτού το box επιτρέπεται το swipe down
    PullToRefreshBox(
        modifier=Modifier.fillMaxSize(),
        isRefreshing = isLoading,
        onRefresh = {
            airViewModel.refreshData(context)
        }
    ){
        Column(modifier=Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)){
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)){
                Column(modifier = Modifier.padding(24.dp)){
                    Row(verticalAlignment=Alignment.CenterVertically){
                        Icon(Icons.Default.Place, contentDescription = "Location")
                        Spacer(modifier=Modifier.width(8.dp))
                        Column{
                            Text(text=userLocation,fontSize=22.sp, fontWeight= FontWeight.Bold,color=MaterialTheme.colorScheme.onPrimary)
                            Text(text=time,color=MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Spacer(modifier=Modifier.height(40.dp))
                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween,verticalAlignment=Alignment.CenterVertically) {
                        Column {
                            Text("AQI", fontWeight = FontWeight.Bold,color=MaterialTheme.colorScheme.onPrimary)
                            Text(text = airQuality.toString(), fontSize = 30.sp)
                        }
                        Column {
                            Text("AQI Level", fontWeight = FontWeight.Bold,color=MaterialTheme.colorScheme.onPrimary)
                            Text(
                                text = getAQILevel(airQuality),
                                color = getAQIColor(airQuality),
                                fontSize = 24.sp
                            )
                        }
                        Text(text = getAQIEmoji(airQuality), fontSize = 50.sp)
                    }
                    Spacer(modifier=Modifier.height(40.dp))
                    Row(modifier=Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                        Column(horizontalAlignment=Alignment.CenterHorizontally){
                            Text("\uD83C\uDF21\uFE0F Temp",fontSize=12.sp,color=MaterialTheme.colorScheme.onPrimary)
                            Text("${weatherData?.tp ?:"--"}°C",fontSize=16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment=Alignment.CenterHorizontally){
                            Text("\uD83D\uDCA7 Humidity",fontSize=12.sp,color=MaterialTheme.colorScheme.onPrimary)
                            Text("${weatherData?.hu ?:"--"}%",fontSize=16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment=Alignment.CenterHorizontally){
                            Text("\uD83D\uDCA8 Wind",fontSize=12.sp,color=MaterialTheme.colorScheme.onPrimary)
                            Text("${weatherData?.ws ?:"--"} m/s",fontSize=16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            //κάρτα συμβουλών υγείας
            Card(shape = RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp),colors= CardDefaults.cardColors(containerColor = healthAdviceData.ContainerColor.copy(alpha=0.15f))){
                Row(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalAlignment = Alignment.CenterVertically){
                    Text(text=healthAdviceData.icon, fontSize = 45.sp)
                    Spacer(modifier=Modifier.width(16.dp))
                    Column{
                        Text(text=healthAdviceData.title, fontWeight = FontWeight.ExtraBold, fontSize = 18.sp,color=healthAdviceData.ContainerColor)
                        Text(text=healthAdviceData.description, fontSize = 14.sp, lineHeight = 18.sp,color= MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            //κάρτα κινδύνου πυρκαγιάς
            Card(shape=RoundedCornerShape(20.dp), elevation = CardDefaults.cardElevation(8.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF333333))){
                Row(modifier = Modifier.fillMaxWidth().padding(24.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically){
                    Column{
                        Text(text="FIRE DANGER INDEX", fontWeight = FontWeight.Bold, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                        Text(text=getFireRiskLevel(calcFWI),color=getFireRiskLevelColor(calcFWI), fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                        Text(text="FWI: ${String.format("%.1f",calcFWI)}",color=Color.White.copy(alpha = 0.5f), fontSize = 14.sp)//νούμερο με ένα δεκαδικό ψηφίο
                    }
                    Text(text=getFireRiskLevelEmoji(calcFWI), fontSize = 50.sp)
                }
            }
        }
    }
}
fun getCityName(context:Context,lat:Double,lon: Double):String{
    return try{
        val geocoder = Geocoder(context, Locale.getDefault())
        val address = geocoder.getFromLocation(lat,lon,1)
        address?.firstOrNull()?.locality ?: "Unknown location"
    }catch(e:Exception){
        "Unknown Location"
    }
}
fun getAQILevel(aqi:Int):String{
    return when{
        aqi <= 50 -> "Good"
        aqi <= 100 ->"Moderate"
        aqi <= 150 || aqi <=200 ->"Unhealthy"
        aqi <= 300 ->"Very unhealthy"
        aqi > 300 -> "Toxic"
        else ->"Unknown"
    }
}
fun getAQIColor(aqi:Int): Color{
    return when{
        aqi <= 50 ->Color.Green
        aqi <= 100 -> Color(0xFF9CCC65)
        aqi <= 150 || aqi <= 200 -> Color.Yellow
        aqi <= 300 -> Color(0xFFFF7043)
        aqi > 300 -> Color.Red
        else -> Color.Gray
    }
}

fun getAQIEmoji(aqi:Int):String{
    return when{
        aqi <= 50 ->"\uD83D\uDE0A"
        aqi <= 100 ->"\uD83D\uDE42"
        aqi <= 150 || aqi <= 200 -> "\uD83D\uDE10"
        aqi <= 300 -> "\uD83D\uDE37"
        aqi > 300 -> "☠☠\uFE0F"
        else -> "?"
    }
}

fun getFireRiskLevel(calcFWI:Double):String{
    return when{
        calcFWI < 5.2 -> "Very Low Chance"
        calcFWI < 11.2 -> "Low Chance"
        calcFWI < 21.3 -> "Moderate Chance"
        calcFWI < 38.0 -> "High Chance"
        calcFWI < 50.0 -> "Very High Chance"
        calcFWI >= 50.0 -> "Extreme Chance"
        else -> "Unknown"
    }
}
fun getFireRiskLevelEmoji(calcFWI: Double):String{
    return when{
        calcFWI < 5.2 -> "\uD83D\uDCA7"
        calcFWI < 11.2 -> "\uD83C\uDF3F"
        calcFWI < 21.3 -> "⚠\uFE0F"
        calcFWI < 38.0 -> "\uD83D\uDD25"
        calcFWI < 50.0 -> "\uD83D\uDE92"
        calcFWI >= 50.0 -> "\uD83C\uDF0B"
        else -> "?"
    }
}
fun getFireRiskLevelColor(calcFWI: Double):Color{
    return when{
        calcFWI < 5.2 -> Color(0xFF2196F3)
        calcFWI < 11.2 -> Color(0xFF4CAF50)
        calcFWI < 21.3 -> Color(0xFFFFEB3B)
        calcFWI < 38.0 -> Color(0xFFFFA000)
        calcFWI < 50.0 -> Color(0xFFFF5722)
        calcFWI >= 50.0 -> Color(0xFFB71C1C)
        else -> Color.Gray
    }
}

fun getHealthAdvice(aqi:Int): HealthAdvice{
    return when{
        aqi <= 50 -> HealthAdvice(
            title = "Ideal Conditions",
            description = "Air quality is considered satisfactory, and air pollution poses little or no risk",
            icon = "\uD83C\uDFC3\u200D♂\uFE0F",
            ContainerColor = Color.Green
        )
        aqi <= 100 -> HealthAdvice(
            title = "Average Quality",
            description = "The air is acceptable. Sensitive individuals may need to reduce strenuous exercise outdoors.",
            icon = "\uD83D\uDEB6\u200D♀\uFE0F",
            ContainerColor = Color.Yellow
        )
        aqi <= 150 -> HealthAdvice(
            title="Harmful for Sensitive People",
            description = "Members of sensitive groups may experience health effects. The general public is not likely to be affected.",
            icon = "\uD83D\uDE10",
            ContainerColor = Color(0xFFFF9800)
        )
        aqi <= 200 -> HealthAdvice(
            title = "Unhealthy",
            description = "Everyone may begin to experience health effects; members of sensitive groups may experience more serious health effects",
            icon = "\uD83D\uDE10",
            ContainerColor = Color.Red
        )
        aqi <= 300 -> HealthAdvice(
            title = "Very Unhealthy",
            description = "Health warnings of emergency conditions. The entire population is more likely to be affected.",
            icon = "\uD83D\uDE37",
            ContainerColor = Color(0xFF9C27B0)
        )
        else -> HealthAdvice(
            title = "Dangerous Conditions",
            description = "Health alert: everyone may experience more serious health effects.Everyone should avoid all outdoor exertion",
            icon = "☠\uFE0F",
            ContainerColor = Color.Black
        )
    }
}