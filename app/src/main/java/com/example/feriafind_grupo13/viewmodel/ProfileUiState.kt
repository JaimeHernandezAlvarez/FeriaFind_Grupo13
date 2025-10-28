package com.example.feriafind_grupo13.viewmodel

import android.net.Uri
data class ProfileUiState(
    val isLoading: Boolean = true,
    val id: Long = 0L, // Guardamos el ID del usuario para saber cuál actualizar
    val nombre: String = "",
    val descripcion: String = "",
    val correo: String = "", // El correo no será editable
    val horario: String = "",
    val fotoUri: Uri? = null,
    val error: String? = null
)
