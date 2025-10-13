package com.example.feriafind_grupo13.navigation

/**
 * Sealed class para definir todas las rutas de la aplicación de forma segura.
 */
sealed class Screen(val route: String) {
    // --- Pantallas de Autenticación ---
    data object Home : Screen(route = "home_screen") // Pantalla inicial con botones de Login/Registro
    data object Login : Screen(route = "login_screen")
    data object Register : Screen(route = "register_screen")

    // --- Contenedor Principal con BottomNavBar ---
    data object Main : Screen(route = "main_screen") // Ruta que aloja las 5 pantallas principales
}

/**
 * Sealed class para las pantallas DENTRO de la barra de navegación principal.
 * Esto ayuda a organizar las rutas y a construir la BottomNavBar fácilmente.
 */
sealed class MainScreen(val route: String, val title: String) {
    data object Map : MainScreen("map_screen", "Mapa")
    data object Products : MainScreen("products_screen", "Productos")
    data object Sellers : MainScreen("sellers_screen", "Vendedores")
    data object Favorites : MainScreen("favorites_screen", "Favoritos")
    data object Profile : MainScreen("profile_screen", "Mi Perfil")
}
