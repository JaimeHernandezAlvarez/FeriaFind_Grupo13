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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
// --- IMPORTAR DEPENDENCIAS ---
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.viewmodel.ProfileViewModel
import com.example.feriafind_grupo13.viewmodel.ProfileViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    repository: UserRepository // <-- 1. ACEPTA EL REPOSITORIO
) {
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

    // --- 2. CREAR LA FACTORY Y EL VIEWMODEL ---
    // (Esto asegura que el viewModel sobreviva mientras estés en MainScreen)
    val profileFactory = ProfileViewModelFactory(repository)
    val profileViewModel: ProfileViewModel = viewModel(factory = profileFactory)
    // --- FIN DE LA MODIFICACIÓN ---

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
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                    )
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

                // 3. PASAR EL VIEWMODEL A LA PANTALLA DE PERFIL
                composable(MainScreenRoute.Profile.route) {
                    ProfileScreen(
                        viewModel = profileViewModel
                    )
                }
            }
        }
    }
}