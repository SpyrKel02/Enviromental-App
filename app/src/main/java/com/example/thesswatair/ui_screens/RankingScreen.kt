package com.example.thesswatair.ui_screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.thesswatair.api.CityRankingData
import com.example.thesswatair.viewmodel.AirQViewModel
@Composable
fun RankingScreen(airViewModel: AirQViewModel){

    val rankingCities = airViewModel.cityRankings

    Column(
        modifier= Modifier.fillMaxSize().background(Color.White)
    ){
        Text(
            text="World AQI Ranking",
            style= MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier=Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            Text("#", modifier = Modifier.width(30.dp), fontWeight = FontWeight.Bold)
            Text("MAJOR CITIES",modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold)
            Text("US AQI", fontWeight = FontWeight.Bold)
        }
        HorizontalDivider()

        LazyColumn(
            modifier=Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom=16.dp)
        ){
            itemsIndexed(items=rankingCities){
                index,item ->
                CityRow(rank=index+1,data=item)
                HorizontalDivider(modifier=Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp)
            }
        }
    }
}

@Composable
fun CityRow(rank:Int,data: CityRankingData){

    val aqiColor = when{
        data.pollution.aqius <= 50 ->Color.Green
        data.pollution.aqius <= 100 -> Color(0xFF9CCC65)
        data.pollution.aqius <= 150 || data.pollution.aqius <= 200 -> Color.Yellow
        data.pollution.aqius <= 300 -> Color(0xFFFF7043)
        data.pollution.aqius > 300 -> Color.Red
        else -> Color.Gray
    }

    Row(
        modifier=Modifier.padding(16.dp).fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Text(text=rank.toString(),modifier=Modifier.width(30.dp))
        Column(modifier=Modifier.weight(1f)){
            Text(text=data.city, fontWeight = FontWeight.Bold)
            Text(text=data.country,style= MaterialTheme.typography.bodySmall,color=Color.Gray)
        }
        Surface(
            color=aqiColor,
            shape= RoundedCornerShape(8.dp),
            modifier=Modifier.width(55.dp)
        ){
            Text(
                text=data.pollution.aqius.toString(),
                color=Color.White,
                textAlign = TextAlign.Center,
                modifier=Modifier.padding(vertical = 4.dp),
                fontWeight = FontWeight.Bold
            )
        }
    }
}