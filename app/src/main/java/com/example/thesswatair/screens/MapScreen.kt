package com.example.thesswatair.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.thesswatair.other.SelectedAreaInfo
import com.google.maps.android.compose.Circle

@Composable
fun MapScreen(airViewModel: AirQViewModel) {
    //βασικές μεταβλητές θέσης και κατάστασης χάρτη
    val userPos = remember(airViewModel.lastLat, airViewModel.lastLon) {
        LatLng(airViewModel.lastLat, airViewModel.lastLon)
    }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(userPos, 10f)
    }

    var isMapReady by remember { mutableStateOf(false) }// Flag για να ξέρουμε πότε ο χάρτης είναι έτοιμος

    LaunchedEffect(Unit){
        airViewModel.fetchAreasData()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true),
            uiSettings = MapUiSettings(zoomControlsEnabled = true),
            onMapLoaded = { isMapReady = true }
        ) {
            if (isMapReady) {
                val currentMode = airViewModel.showMode
                val currentAQI = airViewModel.getAQI()
                val currentFWI = airViewModel.getFWI()
                val FWIpercentage = (currentFWI/ 50.0 * 100).coerceIn(0.0,100.0)

                val color = if (currentMode) getFWIMapColor(currentFWI) else getAQIMapColor(currentAQI)
                val text = if (currentMode) "${String.format("%.1f", FWIpercentage)}%" else currentAQI.toString()
                //position χρήστη
                key(currentMode){
                    if(currentMode != false){
                        Circle(
                            center = userPos,
                            radius = 1000.0,//ακτίνα 1 χιλιομέτρων
                            fillColor = color.copy(alpha = 0.2f),
                            strokeColor = color,//χρώμα περιγράμματος
                            strokeWidth = 2f//πάχος της γραμμής του περιγράμματος
                        )
                    }
                    MarkerComposable(
                        state = MarkerState(position = userPos),
                        title = "My position (LIVE)",
                        zIndex = 10f,
                        onClick = { true }
                    ) {
                        CustomMapMarker(
                            text = "Me",
                            value = text,
                            color = color,
                            isMe = true
                        )
                    }
                }
                key(currentMode){
                    airViewModel.areas.forEach { area ->
                        val distance = airViewModel.getDistanceToArea(
                            userPos.latitude,
                            userPos.longitude,
                            area.coord
                        )
                        if(distance > 1500){
                            val metrics = airViewModel.areasState[area.name]
                            val displayValue = when{
                                metrics == null || metrics.isLoading -> ""
                                currentMode -> "${String.format("%.1f", (metrics.fwi / 50.0 * 100).coerceIn(0.0, 100.0))}%"
                                else -> metrics.aqi.toString()
                            }
                            val markerColor = when{
                                metrics == null || metrics.isLoading -> Color.Gray
                                currentMode -> getFWIMapColor(metrics.fwi)
                                else -> getAQIMapColor(metrics.aqi)
                            }
                            if(currentMode != false){
                                Circle(
                                    center = area.coord,
                                    radius = 1000.0,
                                    fillColor = markerColor.copy(alpha = 0.1f),
                                    strokeColor = markerColor.copy(alpha = 0.3f),
                                    strokeWidth = 1f
                                )
                            }
                            MarkerComposable(
                                state = MarkerState(position = area.coord),
                                title = area.name,
                                zIndex = 5f,
                                onClick = {
                                    Log.d("MapClick", "Πατήθηκε ο Marker της περιοχής: ${area.name}")
                                    true
                                }
                            ){
                                CustomMapMarker(
                                    text = area.name,
                                    value = displayValue,
                                    color = markerColor.copy(alpha = 0.85f),
                                    isMe = false
                                )
                            }
                        }
                    }
                }
            }
        }
        //κουμπιά αλλαγής mode
        Column(modifier = Modifier.align(Alignment.TopStart).padding(16.dp).statusBarsPadding()) {
            MapModeButton(
                text = "Ποιότητα Αέρα",
                isSelected = !airViewModel.showMode,
                color = Color(0xFF4CAF50),
                onClick = { airViewModel.showMode = false }
            )
            Spacer(modifier = Modifier.height(8.dp))
            MapModeButton(
                text = "Κίνδυνος Πυρκαγιάς",
                isSelected = airViewModel.showMode,
                color = Color(0xFFE91E63),
                onClick = { airViewModel.showMode = true }
            )
        }
    }
}
@Composable
fun CustomMapMarker(
    text: String,
    value: String,
    color: Color,
    isMe: Boolean
){
    Box(modifier = Modifier
        .background(color, RoundedCornerShape(12.dp))
        .border(if(isMe) 3.dp else 1.dp, Color.White, RoundedCornerShape(12.dp))
        .padding(8.dp)
    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text(text, fontSize = 10.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun InfoRow(
    text: String,
    value: String
){
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = text, fontSize = 14.sp, color = Color.DarkGray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
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
fun getFWIMapColor(fwi:Double):Color{
    return when {
        fwi <= 5.2 -> Color(0xFF4CAF50)
        fwi <= 11.2 -> Color(0xFF8BC34A)
        fwi <= 21.3 -> Color(0xFFFFEB3B)
        fwi <= 38.0 -> Color(0xFFFF9800)
        fwi > 38.0 -> Color(0xFFF44336)
        else -> Color.Gray
    }
}
@Composable
fun MapModeButton(
    text:String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
){
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if(isSelected) color else Color.White,
            contentColor = if(isSelected) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = ButtonDefaults.buttonElevation(4.dp)
    ){
        Text(text=text, fontSize = 12.sp)
    }
}