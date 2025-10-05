package com.example.feriafind_grupo13.navigation
// Sealed class para definir rutas tipo-safe en la navegación

sealed class Screen(val route: String) {
    // ----------------------------------------------------------
    // Rutas simples (sin argumentos): Usamos 'data object'
    // 'data object' es un singleton seguro de tipos, introducido en Kotlin 1.9+.
    // Si usas una versión anterior de Kotlin, usa 'object' en su lugar.
    // ----------------------------------------------------------

    // Representa la ruta a la pantalla de Inicio.
    data object Home : Screen(route = "home_page")

    // Representa la ruta a la pantalla de Registro.
    data object Register : Screen(route = "register_page")

    // Representa la ruta a la pantalla de Configuración.
    data object Profile : Screen(route = "profile_page")

    // Representa la ruta a la pantalla de Ajustes.
    data object Settings : Screen(route = "settings_page")

    // ----------------------------------------------------------
    // Ejemplos de Rutas con Argumentos (No usados en este ejercicio, pero importante)
    // Usamos 'data class' para encapsular los argumentos.
    // Esto asegura que, al navegar, los argumentos correctos sean pasados y tipados.
    // ----------------------------------------------------------

    /**
     * Ejemplo de una ruta a una pantalla de detalles que requiere un 'itemId'.
     *
     * @param itemId EL ID del elemento a mostrar en la pantalla de detalles.
     */
    data class Detail(val itemId: String) : Screen(route = "detail_page/{itemId}") {
        // Función para construir la ruta final con el argumento.
        // Esto evita errores al crear la ruta string.
        fun buildRoute(): String {
            // Reemplaza el placeholder "{itemId}" en la ruta base con el valor real.
            return route.replace(oldValue = "{itemId}", newValue = itemId)
        }
    }

    // Si tuvieras más argumentos, se agregarían a la data class y a la cadena de ruta.
    // data class UserProfile(val userId: String, val userName: String?) :
    //     AppDestinations("profile/{userId}?name={userName}") { ... }
}
