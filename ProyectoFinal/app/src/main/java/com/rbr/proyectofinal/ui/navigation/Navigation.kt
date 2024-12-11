package com.rbr.proyectofinal.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.rbr.proyectofinal.screens.splash.SplashScreen
import com.rbr.proyectofinal.screens.login.LoginScreen
import com.rbr.proyectofinal.screens.home.HomeScreen
import com.rbr.proyectofinal.screens.profile.ProfileScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screens.SplashScreen.name
    ) {
        composable(Screens.SplashScreen.name) {
            SplashScreen(navController = navController)
        }
        composable(Screens.LoginScreen.name) {
            LoginScreen(navController = navController)
        }
        composable(Screens.HomeScreen.name) {
            HomeScreen(navController = navController)
        }
        composable(
            route = "${Screens.ProfileScreen.name}/{userId}",
            arguments = listOf(navArgument("userId") { type = NavType.StringType })
        ) { backStackEntry ->
            val userIdArgs = backStackEntry.arguments?.getString("userId") ?: ""
            ProfileScreen(navController = navController, userId = userIdArgs)
        }
    }
}
