package com.example.feriafind_grupo13.navigation

sealed class Screen(val route: String) {
    // --- Pantallas de Autenticación ---
    data object Home : Screen(route = "home_screen") // Ruta de inicio
    data object Login : Screen(route = "login_screen")
    data object Register : Screen(route = "register_screen")
    // --- Contenedor Principal con BottomNavBar ---
    data object Main : Screen(route = "main_screen") // Ruta que aloja las 5 pantallas principales

    // Rutas para CRUD (arg id es opcional o -1 para crear)
    data object AddEditProduct : Screen(route = "add_edit_product_screen?productId={productId}") {
        fun createRoute(productId: Int = -1) = "add_edit_product_screen?productId=$productId"
    }

    data object AddEditSeller : Screen(route = "add_edit_seller_screen?sellerId={sellerId}") {
        fun createRoute(sellerId: String = "") = "add_edit_seller_screen?sellerId=$sellerId"
    }
}
sealed class MainScreen(val route: String, val title: String) {
    data object Map : MainScreen("map_screen", "Mapa")
    data object Products : MainScreen("products_screen", "Productos")
    data object Sellers : MainScreen("sellers_screen", "Vendedores")
    data object Favorites : MainScreen("favorites_screen", "Favoritos")
    data object Profile : MainScreen("profile_screen", "Mi Perfil")
}
