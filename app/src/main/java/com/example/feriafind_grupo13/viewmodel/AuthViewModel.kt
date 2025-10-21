package com.example.feriafind_grupo13.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import com.example.feriafind_grupo13.data.model.AuthUiState
import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    // StateFlow para mantener el estado de la UI
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // SharedFlow para manejar eventos de navegación únicos
    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()


    // --- Funciones para manejar cambios en los campos de texto ---

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errorNombre = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorEmail = null) }
    }

    fun onContrasenaChange(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null) }
    }

    fun onConfirmarContrasenaChange(confirmarContrasena: String) {
        _uiState.update { it.copy(confirmarContrasena = confirmarContrasena, errorConfirmarContrasena = null) }
    }

    // --- Funciones de validación y acciones ---

    fun iniciarSesion() {
        val emailError = validateEmail()
        val contrasenaError = validatePasswordForLogin()

        _uiState.update {
            it.copy(
                errorEmail = emailError,
                errorContrasena = contrasenaError
            )
        }

        val isValid = emailError == null && contrasenaError == null
        if (isValid) {
            CoroutineScope(Dispatchers.Main).launch {
                _navigationEvents.emit(NavigationEvent.NavigateTo(
                    route = Screen.Main,
                    popUpToRoute = Screen.Home,
                    inclusive = true
                ))
            }
        }
    }

    fun registrarUsuario() {
        val nombreError = validateName()
        val emailError = validateEmail()
        val contrasenaError = validatePasswordForRegistration()
        val confirmarContrasenaError = validateConfirmPassword()

        _uiState.update {
            it.copy(
                errorNombre = nombreError,
                errorEmail = emailError,
                errorContrasena = contrasenaError,
                errorConfirmarContrasena = confirmarContrasenaError
            )
        }

        val isValid = listOfNotNull(nombreError, emailError, contrasenaError, confirmarContrasenaError).isEmpty()
        if (isValid) {
            println("¡Validación de registro exitosa!")
            CoroutineScope(Dispatchers.Main).launch {
                _navigationEvents.emit(NavigationEvent.NavigateTo(
                    route = Screen.Main,
                    popUpToRoute = Screen.Home,
                    inclusive = true
                ))
            }
        }
    }

    // --- Validadores privados y reutilizables ---

    private fun validateName(): String? {
        val nombre = _uiState.value.nombre
        return if (nombre.isBlank()) "El nombre no puede estar vacío" else null
    }

    private fun validateEmail(): String? {
        val email = _uiState.value.email
        return when {
            email.isBlank() -> "El correo no puede estar vacío"
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Ingresa un formato de correo válido"
            !email.endsWith(".com") -> "El correo debe terminar en .com"
            else -> null
        }
    }

    private fun validatePasswordForLogin(): String? {
        val contrasena = _uiState.value.contrasena
        return when {
            contrasena.isBlank() -> "La contraseña no puede estar vacía"
            contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            else -> null
        }
    }

    private fun validatePasswordForRegistration(): String? {
        val contrasena = _uiState.value.contrasena
        return when {
            contrasena.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            else -> null
        }
    }

    private fun validateConfirmPassword(): String? {
        val contrasena = _uiState.value.contrasena
        val confirmarContrasena = _uiState.value.confirmarContrasena
        return if (contrasena != confirmarContrasena) "Las contraseñas no coinciden" else null
    }
}


