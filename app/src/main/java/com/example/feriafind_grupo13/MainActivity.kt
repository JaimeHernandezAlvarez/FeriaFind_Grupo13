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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.feriafind_grupo13.viewmodel.AppViewModelFactory
import com.example.feriafind_grupo13.viewmodel.AuthViewModel
import com.example.feriafind_grupo13.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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
                    // --- INICIALIZACIÓN DE DEPENDENCIAS ---
                    val context = LocalContext.current.applicationContext

                    // Base de datos y DAOs
                    val db = AppDatabase.getInstance(context)
                    val userDao = db.userDao()
                    val favoriteDao = db.favoriteDao()

                    // Preferencias y Repositorio
                    val userPrefs = UserPreferences(context)
                    val repository = UserRepository(userDao, userPrefs)

                    // ViewModels y Navegación
                    // Usamos la factory solo para AuthViewModel aquí, MainScreen crea la suya propia
                    val userFactory = AppViewModelFactory(userRepository = repository)
                    val authViewModel: AuthViewModel = viewModel(factory = userFactory)
                    val mainViewModel: MainViewModel = viewModel()

                    val navController = rememberNavController()
                    val scope = rememberCoroutineScope() // Para lanzar corrutinas en la UI (Logout)

                    // --- EVENTOS DE NAVEGACIÓN ---
                    // Escuchar eventos de navegación del MainViewModel
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

                    // Escuchar eventos de AuthViewModel (Login/Registro exitoso)
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

                    // --- ESTRUCTURA DE PANTALLAS ---
                    Scaffold(
                        modifier = Modifier.fillMaxSize()
                    ) { innerPadding ->
                        NavHost(
                            navController = navController,
                            startDestination = Screen.Home.route,
                            modifier = Modifier.padding(innerPadding)
                        ) {
                            // Animaciones constantes
                            val animDuration = 400
                            val slideOffset = 1000

                            // 1. PANTALLA DE INICIO (HOME)
                            composable(
                                route = Screen.Home.route,
                                exitTransition = {
                                    slideOutHorizontally(targetOffsetX = { -slideOffset }, animationSpec = tween(animDuration)) + fadeOut(tween(animDuration))
                                },
                                popEnterTransition = {
                                    slideInHorizontally(initialOffsetX = { -slideOffset }, animationSpec = tween(animDuration)) + fadeIn(tween(animDuration))
                                }
                            ) {
                                HomeScreenCompacta(navController, mainViewModel)
                            }

                            // 2. PANTALLA DE REGISTRO
                            composable(
                                route = Screen.Register.route,
                                enterTransition = {
                                    slideInHorizontally(initialOffsetX = { slideOffset }, animationSpec = tween(animDuration)) + fadeIn(tween(animDuration))
                                },
                                exitTransition = {
                                    slideOutHorizontally(targetOffsetX = { -slideOffset }, animationSpec = tween(animDuration)) + fadeOut(tween(animDuration))
                                },
                                popExitTransition = {
                                    slideOutHorizontally(targetOffsetX = { slideOffset }, animationSpec = tween(animDuration)) + fadeOut(tween(animDuration))
                                }
                            ) {
                                RegisterScreen(navController = navController, viewModel = authViewModel)
                            }

                            // 3. PANTALLA DE LOGIN
                            composable(
                                route = Screen.Login.route,
                                enterTransition = {
                                    slideInHorizontally(initialOffsetX = { slideOffset }, animationSpec = tween(animDuration)) + fadeIn(tween(animDuration))
                                },
                                exitTransition = {
                                    slideOutHorizontally(targetOffsetX = { -slideOffset }, animationSpec = tween(animDuration)) + fadeOut(tween(animDuration))
                                },
                                popExitTransition = {
                                    slideOutHorizontally(targetOffsetX = { slideOffset }, animationSpec = tween(animDuration)) + fadeOut(tween(animDuration))
                                }
                            ) {
                                LoginScreen(navController = navController, viewModel = authViewModel)
                            }

                            // 4. PANTALLA PRINCIPAL (DASHBOARD)
                            composable(
                                route = Screen.Main.route,
                                enterTransition = {
                                    slideInHorizontally(initialOffsetX = { slideOffset }, animationSpec = tween(animDuration)) + fadeIn(tween(animDuration))
                                },
                                exitTransition = {
                                    slideOutHorizontally(targetOffsetX = { -slideOffset }, animationSpec = tween(animDuration)) + fadeOut(tween(animDuration))
                                }
                            ) {
                                MainScreen(
                                    userRepository = repository,
                                    favoriteDao = favoriteDao,
                                    onLogout = {
                                        // LÓGICA DE LOGOUT SEGURA
                                        scope.launch {
                                            // 1. Limpiamos la sesión en DataStore
                                            userPrefs.clearSession()
                                            // 2. Navegamos al inicio y borramos el historial
                                            navController.navigate(Screen.Home.route) {
                                                popUpTo(0) { inclusive = true }
                                                launchSingleTop = true
                                            }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}