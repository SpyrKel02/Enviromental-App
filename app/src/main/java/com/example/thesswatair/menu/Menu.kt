package com.example.thesswatair.menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.runtime.setValue
import com.example.thesswatair.navigation.NavigationHost
import com.example.thesswatair.ui_screens.Screen
import com.example.thesswatair.viewmodel.AirQViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Menu(airViewModel: AirQViewModel){
    val menuItems=listOf(
        MenuItems(
            id = Screen.Dashboard.route,
            title = "My Position",
            contentDescription = "Go to the dashboard",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        MenuItems(
            id = Screen.Map.route,
            title = "Map",
            contentDescription = "Go to the map",
            selectedIcon = Icons.Filled.Place,
            unselectedIcon = Icons.Outlined.Place
        ),
        MenuItems(
            id = Screen.Rankings.route,
            title = "Ranking",
            contentDescription = "Go to the ranking",
            selectedIcon = Icons.Filled.List,
            unselectedIcon = Icons.Outlined.List
        ),
        MenuItems(
            id = Screen.Searching.route,
            title="Search stations in other cities",
            contentDescription = "Go to the searching of other cities",
            selectedIcon = Icons.Filled.Search,
            unselectedIcon = Icons.Outlined.Search
        )
    )
    Surface(
        modifier = Modifier.fillMaxSize(),color= MaterialTheme.colorScheme.background
    ){
        val navController=rememberNavController()
        val drawerState= rememberDrawerState(DrawerValue.Closed)
        val scope= rememberCoroutineScope()
        var selectedItemIndex by rememberSaveable { mutableStateOf(0) }

        val currentBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute=currentBackStackEntry?.destination?.route

        ModalNavigationDrawer(
            drawerContent = {
                ModalDrawerSheet {
                    Spacer(Modifier.height(16.dp))
                    menuItems.forEachIndexed{index, item ->
                        NavigationDrawerItem(
                            label = {Text(text=item.title)},
                            selected = index == selectedItemIndex,
                            onClick = {
                                selectedItemIndex=index
                                scope.launch { drawerState.close()
                                 if(currentRoute!=item.id){
                                     navController.navigate(item.id){launchSingleTop=true
                                     popUpTo(Screen.Dashboard.route)}
                                 }
                                }
                            },
                            icon={Icon(imageVector = if(selectedItemIndex==index){item.selectedIcon}else item.unselectedIcon, contentDescription = item.title)},
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )

                    }
                }
            },
            drawerState=drawerState
        ){
            Scaffold(
                topBar={
                    TopAppBar(
                        modifier=Modifier.background(MaterialTheme.colorScheme.primary),
                        title={Text("BreatheAir")},
                        navigationIcon = { IconButton(onClick = {scope.launch { drawerState.open() }}){Icon(imageVector=Icons.Default.Menu,contentDescription="Menu")}}
                    )
                }
            ){
                innerPadding->
                Box(Modifier.fillMaxSize().padding(innerPadding)){
                    NavigationHost(navController = navController,airViewModel=airViewModel)
                }
            }
        }

    }
}