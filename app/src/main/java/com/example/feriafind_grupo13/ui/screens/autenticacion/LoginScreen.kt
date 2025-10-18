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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.feriafind_grupo13.R
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.components.AuthCard
import com.example.feriafind_grupo13.ui.components.BotonAuth
import com.example.feriafind_grupo13.ui.components.CampoDeTextoAuth
import com.example.feriafind_grupo13.ui.components.CampoDeTextoContrasena
import com.example.feriafind_grupo13.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

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
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        label = "Correo Electrónico",
                        isError = emailError != null,
                        errorMessage = emailError ?: "",
                        keyboardType = KeyboardType.Email
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    CampoDeTextoContrasena(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        label = "Contraseña",
                        isError = passwordError != null,
                        errorMessage = passwordError ?: ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonAuth(
                text = "Iniciar Sesión",
                onClick = {
                    var isValid = true
                    if (!email.contains("@") || email.isBlank()) {
                        emailError = "Ingresa un correo válido"
                        isValid = false
                    }
                    if (password.isBlank()) {
                        passwordError = "La contraseña no puede estar vacía"
                        isValid = false
                    }
                    if (isValid) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}

