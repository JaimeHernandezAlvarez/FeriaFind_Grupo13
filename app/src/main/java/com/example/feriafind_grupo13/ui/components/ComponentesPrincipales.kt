package com.example.feriafind_grupo13.ui.components


import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.feriafind_grupo13.navigation.MainScreen
import com.example.feriafind_grupo13.navigation.Screen

@Composable
fun AppDrawer(
    navController: NavHostController,
    closeDrawer: () -> Unit
) {
    val items = listOf(
        MainScreen.Map,
        MainScreen.Products,
        MainScreen.Sellers,
        MainScreen.Favorites,
        MainScreen.Profile
    )

    // 1. Obtenemos el estado actual de la pila de navegación
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // 2. Obtenemos la ruta actual (ej: "profile")
    val currentRoute = navBackStackEntry?.destination?.route

    ModalDrawerSheet(drawerContainerColor = MaterialTheme.colorScheme.primary) {
        Spacer(Modifier.height(12.dp))

        // Colores para los items (blanco)
        val itemColors = NavigationDrawerItemDefaults.colors(
            unselectedIconColor = MaterialTheme.colorScheme.onPrimary,
            unselectedTextColor = MaterialTheme.colorScheme.onPrimary
        )

        items.forEach { screen ->
            NavigationDrawerItem(
                icon = {
                    when (screen) {
                        MainScreen.Map -> Icon(Icons.Default.LocationOn, contentDescription = screen.title)
                        MainScreen.Products -> Icon(Icons.Default.ShoppingCart, contentDescription = screen.title)
                        MainScreen.Sellers -> Icon(Icons.Default.Search, contentDescription = screen.title)
                        MainScreen.Favorites -> Icon(Icons.Default.Favorite, contentDescription = screen.title)
                        MainScreen.Profile -> Icon(Icons.Default.Person, contentDescription = screen.title)
                    }
                },
                label = { Text(screen.title) },
                selected = (currentRoute == screen.route),
                onClick = {
                    navController.navigate(screen.route)
                    closeDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp) ,
                colors = itemColors
            )
        }
        Divider(modifier = Modifier.padding(vertical = 12.dp))
        NavigationDrawerItem(
            icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar Sesión") },
            label = { Text("Cerrar Sesión") },
            selected = false,
            onClick = {
                // Navegar a la pantalla de Home y limpiar todo el historial de navegación
                navController.navigate(Screen.Home.route) {
                    popUpTo(0) {
                        inclusive = true
                    }
                }
                closeDrawer()
            },
            modifier = Modifier.padding(horizontal = 12.dp),
            colors = itemColors
        )

    }
}

@Composable
fun AuthCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors()
    ) {
        content()
    }
}