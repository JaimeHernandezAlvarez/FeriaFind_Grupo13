package com.example.feriafind_grupo13.data.model

/**
 * Data class que representa el estado completo de la UI para las pantallas de autenticación.
 * Contiene los valores de los campos del formulario y los posibles mensajes de error.
 */
data class AuthUiState(
    val nombre: String = "",
    val email: String = "",
    val contrasena: String = "",
    val confirmarContrasena: String = "",
    // Estados de error para cada campo
    val errorNombre: String? = null,
    val errorEmail: String? = null,
    val errorContrasena: String? = null,
    val errorConfirmarContrasena: String? = null
)