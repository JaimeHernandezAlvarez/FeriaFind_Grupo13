package com.example.feriafind_grupo13.viewmodel

import android.net.Uri
data class ProfileUiState(
    val nombre: String = "",
    val descripcion: String = "",
    val correo: String = "",
    val horario: String = "",
    val fotoUri: Uri? = null // Añadido para la foto de perfil
)
