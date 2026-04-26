package com.example.thesswatair.ui_screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thesswatair.viewmodel.AirQViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun SearchScreen(airViewModel: AirQViewModel){
    var searchQuery by remember{ mutableStateOf("") }
    Column(
        modifier=Modifier.fillMaxSize().padding(16.dp)
    ){
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(text = "Search city... e.g London") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {//εικόνισμα στο τέλος του search bar
                IconButton(onClick = {airViewModel.searchCity(cityName=searchQuery)}){
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            singleLine = true//όταν αληθές το πεδίο κειμένου γίνεται ένα ενιαίο οριζόντια κυλιόμενο πεδίο κειμένου αντί να τυλίγεται σε πολλές γραμμές
        )
        Spacer(modifier=Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
            items(airViewModel.citySearchResults){item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp)
                ){
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = item.station.name,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "AQI: ${item.aqi}",
                            style = MaterialTheme.typography.headlineSmall,
                            color = if ((item.aqi.toIntOrNull() ?:0) > 100) Color.Red else Color.Green
                        )
                    }
                }
            }
        }
    }
}