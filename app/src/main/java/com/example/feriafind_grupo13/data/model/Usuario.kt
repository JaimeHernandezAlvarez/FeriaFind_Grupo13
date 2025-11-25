package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("idUsuario")
    val id: String,
    @SerializedName("nombreUsuario")
    val nombre: String,
    @SerializedName("correoElectronico")
    val email: String,
    @SerializedName("password")
    val password: String = "",
    @SerializedName("descripcion")
    val descripcion: String? = null,
    @SerializedName("foto")
    val fotoUrl: String? = null,
    @SerializedName("horario")
    val horario: String? = null,
    val esVendedor: Boolean = false,
    val vendedoresFavoritos: List<String> = emptyList()
)
