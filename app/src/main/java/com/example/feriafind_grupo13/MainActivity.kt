package com.example.feriafind_grupo13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext // <-- 1. IMPORTAR CONTEXT
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
// --- 2. IMPORTAR TODAS LAS DEPENDENCIAS ---
import com.example.feriafind_grupo13.data.local.database.AppDatabase
import com.example.feriafind_grupo13.data.local.storage.UserPreferences
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.screens.HomeScreenCompacta
import com.example.feriafind_grupo13.ui.screens.autenticacion.LoginScreen
import com.example.feriafind_grupo13.ui.screens.autenticacion.RegisterScreen
import com.example.feriafind_grupo13.ui.screens.principal.MainScreen
import com.example.feriafind_grupo13.ui.theme.FeriaFind_Grupo13Theme
import com.example.feriafind_grupo13.viewmodel.AuthViewModel
import com.example.feriafind_grupo13.viewmodel.AuthViewModelFactory // <-- 3. IMPORTAR LA FACTORY
import com.example.feriafind_grupo13.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeriaFind_Grupo13Theme(dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val mainViewModel: MainViewModel = viewModel()

                    // --- INICIO DE LA MODIFICACIÓN ---

                    // 4. Obtener el contexto de la aplicación
                    val context = LocalContext.current.applicationContext

                    // 5. Crear las dependencias (DB, DAO, Prefs, Repo)
                    val db = AppDatabase.getInstance(context)
                    val userDao = db.userDao()
                    val userPrefs = UserPreferences(context)
                    val repository = UserRepository(userDao, userPrefs)

                    // 6. Crear la Fábrica
                    val authFactory = AuthViewModelFactory(repository)

                    // 7. Crear el AuthViewModel USANDO la fábrica
                    val authViewModel: AuthViewModel = viewModel(factory = authFactory)

                    // --- FIN DE LA MODIFICACIÓN ---

                    val navController = rememberNavController()

                    // Escuchar eventos de navegación del MainViewModel...
                    // (Este código se queda igual)
                    LaunchedEffect(key1 = Unit) {
                        mainViewModel.navigationEvents.collectLatest { event ->
                            when (event) {
                                is NavigationEvent.NavigateTo -> {
                                    navController.navigate(route = event.route.route) {
                                        event.popUpToRoute?.let {
                                            popUpTo(route = it.route) {
                                                inclusive = event.inclusive
                                            }
                                        }
                                        launchSingleTop = event.singleTop
                                        restoreState = true
                                    }
                                }

                                is NavigationEvent.PopBackStack -> navController.popBackStack()
                                is NavigationEvent.NavigateUp -> navController.navigateUp()
                            }
                        }
                    }

                    // Escuchar eventos de navegación del AuthViewModel...
                    // (Este código se queda igual)
                    // NOTA: Este bloque ahora funciona como querías,
                    // pero ya no es estrictamente necesario si tus pantallas
                    // (Login/Register) manejan su propia navegación como
                    // lo hacían en tu código original.
                    // Puedes dejarlo o quitarlo, pero no causará error.
                    LaunchedEffect(key1 = Unit) {
                        authViewModel.navigationEvents.collectLatest { event ->
                            when (event) {
                                is NavigationEvent.NavigateTo -> {
                                    navController.navigate(route = event.route.route) {
                                        event.popUpToRoute?.let {
                                            popUpTo(route = it.route) {
                                                inclusive = event.inclusive
                                            }
                                        }
                                        launchSingleTop = event.singleTop
                                    }
                                }

                                is NavigationEvent.PopBackStack -> navController.popBackStack()
                                is NavigationEvent.NavigateUp -> navController.navigateUp()
                            }
                        }
                    }

                    // Layout base con NavHost
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            composable(route = Screen.Home.route) {
                                HomeScreenCompacta(navController, mainViewModel)
                            }
                            // Estas llamadas ya estaban correctas, pasando el viewModel
                            composable(route = Screen.Register.route) {
                                RegisterScreen(
                                    navController = navController,
                                    viewModel = authViewModel
                                )
                            }
                            composable(route = Screen.Login.route) {
                                LoginScreen(
                                    navController = navController,
                                    viewModel = authViewModel
                                )
                            }
                            composable(route = Screen.Main.route) {
                                MainScreen(repository = repository)
                            }
                        }
                    }
                }
            }
        }
    }
}