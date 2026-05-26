package com.example.thesswatair.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.thesswatair.screens.DashboardScreen
import com.example.thesswatair.screens.MapScreen
import com.example.thesswatair.screens.RankingScreen
import com.example.thesswatair.screens.Screen
import com.example.thesswatair.screens.SearchScreen
import com.example.thesswatair.viewmodel.AirQViewModel
@Composable
fun NavigationHost(navController: NavHostController,airViewModel: AirQViewModel){
    NavHost(
        navController=navController, startDestination = Screen.Dashboard.route
    ){
        composable(route= Screen.Dashboard.route){ DashboardScreen(airViewModel=airViewModel) }
        composable(route= Screen.Map.route){ MapScreen(airViewModel=airViewModel) }
        composable(route= Screen.Rankings.route){ RankingScreen(airViewModel=airViewModel) }
        composable(route = Screen.Searching.route){ SearchScreen(airViewModel=airViewModel) }
    }
}