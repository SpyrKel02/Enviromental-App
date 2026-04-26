package com.example.thesswatair.ui_screens

import android.R
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.thesswatair.viewmodel.AirQViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MarkerComposable
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreen(airViewModel: AirQViewModel){
    //ρύθμιση κάμερας στην τοποθεσία του χρήστη
    val userLat = LatLng(airViewModel.lastLat,airViewModel.lastLon)
    val cameraPositionState = rememberCameraPositionState{
        position = CameraPosition.fromLatLngZoom(userLat,10f)
    }

    LaunchedEffect(Unit){
        airViewModel.loadWorldStations("34.0,19.0,42.0,29.0")//συντεταγμένες για Ελλάδα
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ){
        GoogleMap(
            modifier=Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = true)
        ){
            airViewModel.worldMapStations.forEach { station ->
                val aqiValue = station.aqi.toIntOrNull() ?: 0
                MarkerComposable(
                    state= MarkerState(position = LatLng(station.lat,station.lon)),
                    title = station.station.name,
                    snippet = "AQI: ${station.aqi}"
                ){
                    Box(
                        modifier=Modifier.background(getAQIMapColor(aqiValue),RoundedCornerShape(4.dp))
                            .border(1.dp,Color.White,RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            text = station.aqi,
                            color = if(aqiValue>100) Color.White else Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

fun getAQIMapColor(aqi:Int): Color{
    return when{
        aqi <= 50 ->Color.Green
        aqi <= 100 -> Color(0xFF9CCC65)
        aqi <= 150 || aqi <= 200 -> Color.Yellow
        aqi <= 300 -> Color(0xFFFF7043)
        aqi > 300 -> Color.Red
        else -> Color.Gray
    }
}
