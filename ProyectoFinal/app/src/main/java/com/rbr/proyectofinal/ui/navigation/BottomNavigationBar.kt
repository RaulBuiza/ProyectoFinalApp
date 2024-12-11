package com.rbr.proyectofinal.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        "Home" to Icons.Default.Home,
        "Profile" to Icons.Default.Person
    )

    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val currentRoute = currentDestination?.route?.substringBefore("/")

    val context = LocalContext.current
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""
    var userName by remember { mutableStateOf("Pedro") }

    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener { document ->
                    userName = document.getString("nombre") ?: "Usuario"
                }
                .addOnFailureListener {
                    Log.e("BottomNavigationBar", "Error al obtener el nombre del usuario", it)
                }
        }
    }

    NavigationBar(containerColor = Color.Black) {
        items.forEachIndexed { index, item ->
            val route = when (index) {
                0 -> Screens.HomeScreen.name
                1 -> Screens.ProfileScreen.name
                else -> ""
            }

            val isSelected = currentRoute == route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(
                        if (index == 1) {
                            "${Screens.ProfileScreen.name}/$userName"
                        } else {
                            Screens.HomeScreen.name
                        }
                    )
                },
                icon = {
                    Icon(
                        imageVector = item.second,
                        contentDescription = item.first,
                        modifier = Modifier.size(30.dp)
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = if (isSelected) Color.Gray else Color.Transparent
                )
            )
        }
    }
}
