package com.example.feriafind_grupo13.ui.screens.principal

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.navigation.MainScreen as MainScreenRoute
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.components.AppDrawer
import com.example.feriafind_grupo13.ui.screens.admin.AddEditProductScreen
import com.example.feriafind_grupo13.ui.screens.admin.AddEditSellerScreen
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

    val appFactory = AppViewModelFactory(
        userRepository = userRepository,
        favoriteDao = favoriteDao
    )

    // ViewModels compartidos (para mantener estado al navegar)
    val productsViewModel: ProductsViewModel = viewModel(factory = appFactory)
    val sellersViewModel: SellersViewModel = viewModel(factory = appFactory)

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
                    val viewModel: ProductsViewModel = viewModel(factory = appFactory)
                    ProductListScreen(viewModel = productsViewModel, navController = navController)
                }
                composable(MainScreenRoute.Sellers.route) {
                    val viewModel: SellersViewModel = viewModel(factory = appFactory)
                    SellerListScreen(viewModel = sellersViewModel, navController = navController)
                }
                composable(MainScreenRoute.Favorites.route) {
                    val viewModel: FavoritesViewModel = viewModel(factory = appFactory)
                    FavoritesScreen(viewModel = viewModel)
                }
                composable(MainScreenRoute.Profile.route) {
                    val viewModel: ProfileViewModel = viewModel(factory = appFactory)
                    ProfileScreen(viewModel = viewModel, onLogout = onLogout)
                }
                composable(route = Screen.AddEditProduct.route, arguments = listOf(navArgument("productId") { type = NavType.IntType; defaultValue = -1 })
                ) { backStackEntry ->
                    val productId = backStackEntry.arguments?.getInt("productId") ?: -1
                    AddEditProductScreen(navController, productsViewModel, productId)
                }
                composable(route = Screen.AddEditSeller.route, arguments = listOf(navArgument("sellerId") { type = NavType.StringType; defaultValue = "" })
                ) { backStackEntry ->
                    val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
                    AddEditSellerScreen(navController, sellersViewModel, sellerId)
                }
            }
        }
    }
}