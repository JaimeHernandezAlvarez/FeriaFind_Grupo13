package com.example.feriafind_grupo13.data.model

//Representa el perfil de un vendedor en una feria.

data class Vendedor(
    val id: String,
    val idUsuario: String, // Vincula al Usuario que controla este perfil
    val nombre: String,
    val descripcion: String,
    val horario: String? = null, // Horario de atención del vendedor
    val fotoUrl: String? = null
)
