package com.example.feriafind_grupo13

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.screens.HomeScreenCompacta
import com.example.feriafind_grupo13.ui.screens.autenticacion.LoginScreen
import com.example.feriafind_grupo13.ui.screens.autenticacion.RegisterScreen
import com.example.feriafind_grupo13.ui.screens.principal.MainScreen
import com.example.feriafind_grupo13.ui.theme.FeriaFind_Grupo13Theme
import com.example.feriafind_grupo13.viewmodel.AuthViewModel
import com.example.feriafind_grupo13.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FeriaFind_Grupo13Theme {
                val mainViewModel: MainViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()
                val navController = rememberNavController()

                // Escuchar eventos de navegación del MainViewModel (para la navegación principal)
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

                // Escuchar eventos de navegación del AuthViewModel (para login/registro)
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
                        composable(route = Screen.Register.route) {
                            RegisterScreen(navController = navController, viewModel = authViewModel)
                        }
                        composable(route = Screen.Login.route) {
                            LoginScreen(navController = navController, viewModel = authViewModel)
                        }
                        composable(route = Screen.Main.route) {
                            MainScreen()
                        }
                    }
                }
            }
        }
    }
}

