package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.model.AuthUiState
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: UserRepository
) : ViewModel() {

    // Expresión regular simple para validar emails en cualquier entorno (Android o Tests)
    private val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$".toRegex()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // --- Funciones para cambios de texto (Igual que antes) ---

    fun onNombreChange(nombre: String) {
        _uiState.update { it.copy(nombre = nombre, errorNombre = null, generalError = null) }
    }

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email, errorEmail = null, generalError = null) }
    }

    fun onContrasenaChange(contrasena: String) {
        _uiState.update { it.copy(contrasena = contrasena, errorContrasena = null, generalError = null) }
    }

    fun onConfirmarContrasenaChange(confirmarContrasena: String) {
        _uiState.update { it.copy(confirmarContrasena = confirmarContrasena, errorConfirmarContrasena = null, generalError = null) }
    }

    // --- Acciones ---

    fun iniciarSesion() {
        val emailError = validateEmail()
        val contrasenaError = validatePasswordForLogin()

        _uiState.update {
            it.copy(
                errorEmail = emailError,
                errorContrasena = contrasenaError,
                generalError = null
            )
        }

        val isValid = emailError == null && contrasenaError == null
        if (isValid) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val result = repository.loginUser(
                    email = _uiState.value.email,
                    contrasena = _uiState.value.contrasena
                )

                _uiState.update { it.copy(isLoading = false) }

                result.onSuccess {
                    _navigationEvents.emit(NavigationEvent.NavigateTo(
                        route = Screen.Main,
                        popUpToRoute = Screen.Home,
                        inclusive = true
                    ))
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(generalError = exception.message ?: "Error desconocido")
                    }
                }
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
                errorConfirmarContrasena = confirmarContrasenaError,
                generalError = null
            )
        }

        val isValid = listOfNotNull(nombreError, emailError, contrasenaError, confirmarContrasenaError).isEmpty()
        if (isValid) {
            viewModelScope.launch {
                _uiState.update { it.copy(isLoading = true) }

                val result = repository.registerUser(
                    nombre = _uiState.value.nombre,
                    email = _uiState.value.email,
                    contrasena = _uiState.value.contrasena
                )

                _uiState.update { it.copy(isLoading = false) }

                result.onSuccess {
                    _navigationEvents.emit(NavigationEvent.NavigateTo(
                        route = Screen.Main,
                        popUpToRoute = Screen.Home,
                        inclusive = true
                    ))
                }.onFailure { exception ->
                    _uiState.update {
                        it.copy(generalError = exception.message ?: "Error al registrar")
                    }
                }
            }
        }
    }

    // --- Validadores ---

    private fun validateName(): String? {
        val nombre = _uiState.value.nombre
        return if (nombre.isBlank()) "El nombre no puede estar vacío" else null
    }

    private fun validateEmail(): String? {
        val email = _uiState.value.email
        return when {
            email.isBlank() -> "El correo no puede estar vacío"
            // USAMOS REGEX AQUÍ EN LUGAR DE PATTERNS
            !email.matches(emailRegex) -> "Ingresa un formato de correo válido"
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