package com.example.feriafind_grupo13.ui.screens.autenticacion

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.viewmodel.MainViewModel
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    // Estados para campos
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    // Estados para errores
    var nameError by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf("") }
    var confirmPasswordError by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro - FeriaFind") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
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
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Crea tu cuenta",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Campo Nombre
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    if (it.isNotBlank()) nameError = ""
                },
                label = { Text("Nombre completo") },
                isError = nameError.isNotEmpty(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (nameError.isNotEmpty()) {
                Text(text = nameError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Correo
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    if (it.contains("@")) emailError = ""
                },
                label = { Text("Correo electrónico") },
                isError = emailError.isNotEmpty(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            if (emailError.isNotEmpty()) {
                Text(text = emailError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Contraseña
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    if (it.length >= 6) passwordError = ""
                },
                label = { Text("Contraseña") },
                isError = passwordError.isNotEmpty(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon = if (showPassword)
                        Icons.Default.Visibility
                    else
                        Icons.Default.VisibilityOff

                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(imageVector = icon, contentDescription = "Mostrar contraseña")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            if (passwordError.isNotEmpty()) {
                Text(text = passwordError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            // Campo Confirmar Contraseña
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    if (it == password) confirmPasswordError = ""
                },
                label = { Text("Confirmar contraseña") },
                isError = confirmPasswordError.isNotEmpty(),
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )
            if (confirmPasswordError.isNotEmpty()) {
                Text(text = confirmPasswordError, color = Color.Red, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de Registro
            Button(
                onClick = {
                    // Validaciones
                    var valid = true
                    if (name.isBlank()) {
                        nameError = "El nombre es obligatorio"
                        valid = false
                    }
                    if (!email.contains("@")) {
                        emailError = "Correo inválido"
                        valid = false
                    }
                    if (password.length < 6) {
                        passwordError = "La contraseña debe tener al menos 6 caracteres"
                        valid = false
                    }
                    if (password != confirmPassword) {
                        confirmPasswordError = "Las contraseñas no coinciden"
                        valid = false
                    }

                    if (valid) {
                        // Aquí podrías llamar al ViewModel para registrar
                        // viewModel.registerUser(name, email, password)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Botón para volver al login
            TextButton(onClick = { /*Inicio Sessión*/ }) {
                Text("¿Ya tienes cuenta? Inicia sesión")
            }
        }
    }
}
