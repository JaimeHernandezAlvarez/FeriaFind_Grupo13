package com.example.feriafind_grupo13.ui.screens.autenticacion

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.components.AuthCard
import com.example.feriafind_grupo13.ui.components.BotonAuth
import com.example.feriafind_grupo13.ui.components.CampoDeTextoAuth
import com.example.feriafind_grupo13.ui.components.CampoDeTextoContrasena
import com.example.feriafind_grupo13.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Manejo de eventos de navegación (éxito al login)
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvents.collectLatest { event ->
            if (event is NavigationEvent.NavigateTo) {
                navController.navigate(event.route.route) {
                    event.popUpToRoute?.let { popUpTo(it.route) { inclusive = event.inclusive } }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Inicio de Sesión") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))

            AuthCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    CampoDeTextoAuth(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Correo Electrónico",
                        isError = uiState.errorEmail != null,
                        errorMessage = uiState.errorEmail ?: "",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    CampoDeTextoContrasena(
                        value = uiState.contrasena,
                        onValueChange = viewModel::onContrasenaChange,
                        label = "Contraseña",
                        isError = uiState.errorContrasena != null,
                        errorMessage = uiState.errorContrasena ?: ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECCIÓN DE ERROR GENERAL (Backend / Conexión) ---
            if (uiState.generalError != null) {
                Text(
                    text = uiState.generalError!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                )
            }

            // --- BOTÓN O LOADING ---
            if (uiState.isLoading) {
                // Si está cargando, mostramos la ruedita y ocultamos el botón
                CircularProgressIndicator()
            } else {
                BotonAuth(
                    text = "Iniciar Sesión",
                    onClick = {
                        viewModel.iniciarSesion()
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Deshabilitamos el ir al registro si estamos cargando para evitar bugs visuales
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                enabled = !uiState.isLoading
            ) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}