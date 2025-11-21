package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName

data class Vendedor(
    @SerializedName("idVendedor")
    val id: String,
    @SerializedName("idUsuario")
    val idUsuario: String,
    @SerializedName("nombreVendedor")
    val nombre: String,
    @SerializedName("descripcion")
    val descripcion: String,
    @SerializedName("fotoPerfil")
    val fotoUrl: String? = null,
    val horario: String? = null
)
