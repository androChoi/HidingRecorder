package com.ando.hidingrecorder

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.ando.hidingrecorder.ui.screens.HomeScreen
import com.ando.hidingrecorder.ui.screens.SettingScreen

enum class Screen{
    FIRST,
    HOME,
    SETTING,
}

sealed class NavigationItem(val route : String){
    object First : NavigationItem(Screen.FIRST.name)
    object Home : NavigationItem(Screen.HOME.name)
    object Setting : NavigationItem(Screen.SETTING.name)
}

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    startDestination : String = NavigationItem.Home.route
){
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ){
        composable(NavigationItem.Home.route){
            HomeScreen(navController)
        }
        composable(NavigationItem.Setting.route){
            SettingScreen(navController)
        }
    }
}