package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.feriafind_grupo13.navigation.MainScreen
import com.example.feriafind_grupo13.ui.components.AppDrawer
import kotlinx.coroutines.launch
import com.example.feriafind_grupo13.navigation.MainScreen as MainScreenRoute
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val menuItems = listOf(
        MainScreenRoute.Map,
        MainScreenRoute.Products,
        MainScreenRoute.Sellers,
        MainScreenRoute.Favorites,
        MainScreenRoute.Profile
    )
    val currentScreen = menuItems.find { it.route == currentRoute } ?: MainScreenRoute.Map


    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                navController = navController,
                closeDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(currentScreen.title) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú")
                        }
                    }
                )
            }
        ) { innerPadding ->
            NavHost(
                navController,
                startDestination = MainScreenRoute.Map.route,
                Modifier.padding(innerPadding)
            ) {
                composable(MainScreenRoute.Map.route) { MapScreen() }
                composable(MainScreenRoute.Products.route) { ProductListScreen() }
                composable(MainScreenRoute.Sellers.route) { SellerListScreen() }
                composable(MainScreenRoute.Favorites.route) { FavoritesScreen() }
                composable(MainScreenRoute.Profile.route) { ProfileScreen() }
            }
        }
    }
}