package com.example.thesswatair.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.thesswatair.viewmodel.AirQViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(airViewModel: AirQViewModel){
    var searchQuery by rememberSaveable{ mutableStateOf("") }
    val scope = rememberCoroutineScope()

    val filterResults = airViewModel.citySearchResults.filter { item->
        item.station.name.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier=Modifier.fillMaxSize().padding(16.dp)
    ){
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                airViewModel.searchCity(it)
            },
            label = { Text(text = "Αναζήτησε πόλη... π.χ. London") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {//εικόνισμα στο τέλος του search bar
                if(searchQuery.isNotEmpty()){
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            airViewModel.searchCity("")
                        }
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,//όταν αληθές το πεδίο κειμένου γίνεται ένα ενιαίο οριζόντια κυλιόμενο πεδίο κειμένου αντί να τυλίγεται σε πολλές γραμμές
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Search
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier=Modifier.height(16.dp))

        if(airViewModel.isSearching){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
        else if(filterResults.isEmpty() && searchQuery.isNotEmpty()){
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Κανένας σταθμός σε αυτήν την περιοχή: $searchQuery",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }else{
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)){
                items(airViewModel.citySearchResults){item ->
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable{
                            item.geo?.let { coords ->
                                if(coords.size >= 2 ){
                                    val lat = coords[0]
                                    val lon = coords[1]

                                    airViewModel.lastLat = lat
                                    airViewModel.lastLon = lon

                                    scope.launch {
                                        airViewModel.loadAirQuality(lat, lon)
                                        airViewModel.loadFireRisk(lat, lon)
                                    }
                                }
                            }
                        },
                        elevation = CardDefaults.cardElevation(4.dp)
                    ){
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Icon(
                                contentDescription = null,
                                tint = Color(0xFF5F6368),
                                imageVector = Icons.Default.LocationOn,
                                modifier = Modifier.size(28.dp)
                            )
                            Column(
                                modifier = Modifier.padding(horizontal = 12.dp).weight(1f)
                            ){
                                Text(
                                    text = item.station.name.split(",").first(),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if(item.station.name.contains(",")) item.station.name.substringAfter(", ") else "Station",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.Gray
                                )
                            }
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
}