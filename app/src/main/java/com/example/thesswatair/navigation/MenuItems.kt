package com.example.thesswatair.navigation

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItems(
    val id:String,
    val title:String,
    val contentDescription:String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)