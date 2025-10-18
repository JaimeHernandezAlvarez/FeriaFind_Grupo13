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
fun RegisterScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

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
                        value = name,
                        onValueChange = { name = it; nameError = null },
                        label = "Nombre de usuario",
                        isError = nameError != null,
                        errorMessage = nameError ?: ""
                    )

                    CampoDeTextoAuth(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        label = "Correo Electrónico",
                        isError = emailError != null,
                        errorMessage = emailError ?: "",
                        keyboardType = KeyboardType.Email
                    )

                    CampoDeTextoContrasena(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        label = "Contraseña",
                        isError = passwordError != null,
                        errorMessage = passwordError ?: ""
                    )

                    CampoDeTextoContrasena(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it; confirmPasswordError = null },
                        label = "Confirmar contraseña",
                        isError = confirmPasswordError != null,
                        errorMessage = confirmPasswordError ?: ""
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            BotonAuth(
                text = "Registrarse",
                onClick = {
                    nameError = if (name.isBlank()) "El nombre no puede estar vacío" else null
                    emailError = if (!email.contains("@") || email.isBlank()) "Ingresa un correo válido" else null
                    passwordError = if (password.length < 6) "La contraseña debe tener al menos 6 caracteres" else null
                    confirmPasswordError = if (password != confirmPassword) "Las contraseñas no coinciden" else null

                    if (nameError == null && emailError == null && passwordError == null && confirmPasswordError == null) {
                        navController.navigate(Screen.Main.route) {
                            popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { navController.navigate(Screen.Login.route) }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}

