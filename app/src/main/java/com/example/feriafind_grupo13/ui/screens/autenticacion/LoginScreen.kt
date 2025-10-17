package com.example.feriafind_grupo13.ui.screens.autenticacion

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.ui.components.AuthButton
import com.example.feriafind_grupo13.ui.components.AuthTextField
import com.example.feriafind_grupo13.ui.components.PasswordTextField
import com.example.feriafind_grupo13.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    // Estados para guardar lo que el usuario escribe en los campos.
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estados para controlar los mensajes de error. Son nulos cuando no hay error.
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inicio de Sesión", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // Campo de texto para el email.
            AuthTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null // Limpia el error al escribir.
                },
                label = "Correo Electrónico",
                isError = emailError != null, // Hay error si el mensaje no es nulo.
                errorMessage = emailError ?: "", // Muestra el mensaje de error.
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de texto para la contraseña.
            PasswordTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null // Limpia el error al escribir.
                },
                label = "Contraseña",
                isError = passwordError != null,
                errorMessage = passwordError ?: ""
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Botón para iniciar sesión.
            AuthButton(
                text = "Iniciar Sesión",
                onClick = {
                    var isValid = true
                    // Validación del email.
                    if (!email.contains("@") || email.isBlank()) {
                        emailError = "Ingresa un correo válido"
                        isValid = false
                    }
                    // Validación de la contraseña.
                    if (password.isBlank()) {
                        passwordError = "La contraseña no puede estar vacía"
                        isValid = false
                    }

                    if (isValid) {
                        // Si todo es válido, se intenta hacer login y navegar.
                        // TODO: Implementar la lógica de login en el ViewModel.
                        // viewModel.loginUser(email, password)

                        // Navegamos al contenedor principal `Main` y limpiamos el historial.
                        navController.navigate(Screen.Main.route) {
                            popUpTo(navController.graph.startDestinationId) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(12.dp))

            // Botón de texto para ir a la pantalla de registro.
            TextButton(onClick = { navController.navigate(Screen.Register.route) }) {
                Text("¿No tienes cuenta? Regístrate")
            }
        }
    }
}
