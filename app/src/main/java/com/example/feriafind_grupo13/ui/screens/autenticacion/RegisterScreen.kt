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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.components.AuthCard
import com.example.feriafind_grupo13.ui.components.BotonAuth
import com.example.feriafind_grupo13.ui.components.CampoDeTextoAuth
import com.example.feriafind_grupo13.ui.components.CampoDeTextoContrasena
import com.example.feriafind_grupo13.viewmodel.AuthViewModel
import com.example.feriafind_grupo13.navigation.NavigationEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

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
                title = { Text("Registro de Usuario") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver")
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))

            AuthCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CampoDeTextoAuth(
                        value = uiState.nombre,
                        onValueChange = viewModel::onNombreChange,
                        label = "Nombre de usuario",
                        isError = uiState.errorNombre != null,
                        errorMessage = uiState.errorNombre ?: ""
                    )

                    CampoDeTextoAuth(
                        value = uiState.email,
                        onValueChange = viewModel::onEmailChange,
                        label = "Correo Electrónico",
                        isError = uiState.errorEmail != null,
                        errorMessage = uiState.errorEmail ?: "",
                        keyboardType = KeyboardType.Email
                    )

                    CampoDeTextoContrasena(
                        value = uiState.contrasena,
                        onValueChange = viewModel::onContrasenaChange,
                        label = "Contraseña",
                        isError = uiState.errorContrasena != null,
                        errorMessage = uiState.errorContrasena ?: ""
                    )

                    CampoDeTextoContrasena(
                        value = uiState.confirmarContrasena,
                        onValueChange = viewModel::onConfirmarContrasenaChange,
                        label = "Confirmar contraseña",
                        isError = uiState.errorConfirmarContrasena != null,
                        errorMessage = uiState.errorConfirmarContrasena ?: ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonAuth(
                text = "Registrarse",
                onClick = {
                    viewModel.registrarUsuario()
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

