package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.navigation.MainScreen as MainScreenRoute
import com.example.feriafind_grupo13.ui.components.AppDrawer
import com.example.feriafind_grupo13.viewmodel.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userRepository: UserRepository,
    favoriteDao: FavoriteDao,
    onLogout: () -> Unit = {} // Callback para manejar el cierre de sesión
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

    // --- CORRECCIÓN CRÍTICA ---
    // Creamos una ÚNICA factory con TODAS las dependencias.
    // SellersViewModel y FavoritesViewModel ahora necesitan AMBOS (repo y dao).
    val appFactory = AppViewModelFactory(
        userRepository = userRepository,
        favoriteDao = favoriteDao
    )

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
            NavHost(navController, startDestination = MainScreenRoute.Map.route, Modifier.padding(innerPadding)) {

                composable(MainScreenRoute.Map.route) { MapScreen() }

                composable(MainScreenRoute.Products.route) {
                    // Usamos la factory completa (aunque ProductsVM solo use Repo interno o inyectado)
                    val viewModel: ProductsViewModel = viewModel(factory = appFactory)
                    ProductListScreen(viewModel = viewModel)
                }

                composable(MainScreenRoute.Sellers.route) {
                    // AQUÍ FALLABA: Ahora appFactory tiene userRepository Y favoriteDao
                    val viewModel: SellersViewModel = viewModel(factory = appFactory)
                    SellerListScreen(viewModel = viewModel)
                }

                composable(MainScreenRoute.Favorites.route) {
                    // AQUÍ TAMBIÉN: FavoritesViewModel necesita ambos
                    val viewModel: FavoritesViewModel = viewModel(factory = appFactory)
                    FavoritesScreen(viewModel = viewModel)
                }

                composable(MainScreenRoute.Profile.route) {
                    // ProfileViewModel necesita userRepository
                    val viewModel: ProfileViewModel = viewModel(factory = appFactory)

                    // Pasamos el callback onLogout para que al borrar la cuenta navegue fuera
                    ProfileScreen(
                        viewModel = viewModel,
                        onLogout = onLogout
                    )
                }
            }
        }
    }
}