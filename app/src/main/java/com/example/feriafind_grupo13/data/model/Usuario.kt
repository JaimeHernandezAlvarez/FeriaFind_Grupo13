package com.example.feriafind_grupo13.data.model

/**
 * Representa a un usuario de la aplicación, que puede ser un comprador o un vendedor.
 */
data class Usuario(
    val id: String,
    val nombre: String,
    val email: String,
    val descripcion: String? = null,
    val fotoUrl: String? = null,
    val horario: String? = null,
    val esVendedor: Boolean = false,
    val vendedoresFavoritos: List<String> = emptyList() // Lista de IDs de Vendedores
)
