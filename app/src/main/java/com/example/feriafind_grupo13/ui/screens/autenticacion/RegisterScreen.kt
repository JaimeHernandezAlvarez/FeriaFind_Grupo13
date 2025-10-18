package com.example.feriafind_grupo13.ui.screens.autenticacion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    // PASO 1: Definir estados para los valores de los campos.
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // PASO 2: Definir estados para los mensajes de error de cada campo.
    // Usamos un String que puede ser nulo. Si es nulo, no hay error.
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Crea tu cuenta", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(32.dp))

            // PASO 3: Usar tus componentes `AuthTextField` y `PasswordTextField`.
            // Les pasamos tanto el valor como el estado de error.
            CampoDeTextoAuth(
                value = name,
                onValueChange = {
                    name = it
                    nameError = null // Limpia el error cuando el usuario escribe
                },
                label = "Nombre de usuario",
                isError = nameError != null,
                errorMessage = nameError ?: ""
            )
            Spacer(modifier = Modifier.height(8.dp))

            CampoDeTextoAuth(
                value = email,
                onValueChange = {
                    email = it
                    emailError = null
                },
                label = "Correo Electrónico",
                isError = emailError != null,
                errorMessage = emailError ?: "",
                keyboardType = KeyboardType.Email
            )
            Spacer(modifier = Modifier.height(8.dp))

            CampoDeTextoContrasena(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = null
                },
                label = "Contraseña",
                isError = passwordError != null,
                errorMessage = passwordError ?: ""
            )
            Spacer(modifier = Modifier.height(8.dp))

            CampoDeTextoContrasena(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = null
                },
                label = "Confirmar contraseña",
                isError = confirmPasswordError != null,
                errorMessage = confirmPasswordError ?: ""
            )
            Spacer(modifier = Modifier.height(24.dp))

            // PASO 4: Usar tu componente `AuthButton` y poner la lógica de validación.
            BotonAuth(
                text = "Registrarse",
                onClick = {
                    // Reiniciamos los errores
                    nameError = null
                    emailError = null
                    passwordError = null
                    confirmPasswordError = null
                    var isValid = true

                    // Validaciones
                    if (name.isBlank()) {
                        nameError = "El nombre no puede estar vacío"
                        isValid = false
                    }
                    if (!email.contains("@") || email.isBlank()) {
                        emailError = "Ingresa un correo válido"
                        isValid = false
                    }
                    if (password.length < 6) {
                        passwordError = "La contraseña debe tener al menos 6 caracteres"
                        isValid = false
                    }
                    if (password != confirmPassword) {
                        confirmPasswordError = "Las contraseñas no coinciden"
                        isValid = false
                    }

                    if (isValid) {
                        // TODO: Aquí llamarías al ViewModel para registrar al usuario.
                        // viewModel.registerUser(name, email, password)
                        // Por ahora, navegamos a la pantalla principal.
                    }
                }
            )
        }
    }
}

