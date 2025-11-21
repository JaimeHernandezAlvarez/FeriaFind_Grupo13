package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName
data class Producto(
    @SerializedName("idProducto")
    val id: String,
    @SerializedName("nombreProducto")
    val nombre: String,
    @SerializedName("precio")
    val precio: Double,
    @SerializedName("categoria")
    val categoria: String,
    @SerializedName("imagen")
    val imagenUrl: String? = null, // Puede ser nulo si no hay foto
    @SerializedName("idVendedor")
    val idVendedor: String
)