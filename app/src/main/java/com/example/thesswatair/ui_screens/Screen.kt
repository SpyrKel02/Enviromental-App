package com.example.thesswatair.ui_screens

sealed class Screen(val route:String) {
    object Dashboard:Screen(route = "dashboard_screen")
    object Map:Screen(route="map_screen")
    object Rankings:Screen(route="rankings_screen")
    object Searching:Screen(route="searching_screen")
}