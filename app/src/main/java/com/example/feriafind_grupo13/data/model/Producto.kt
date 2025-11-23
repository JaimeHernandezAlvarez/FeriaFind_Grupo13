package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName
data class Producto(
    @SerializedName("idProducto")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("precio")
    val precio: Double,

    @SerializedName("idCategoria")
    val categoria: Int,

    @SerializedName("imagen")
    val imagenUrl: String? = null,

    @SerializedName("idVendedor")
    val idVendedor: Int,

    @SerializedName("unidadMedida")
    val unidadMedida: String? = null
)