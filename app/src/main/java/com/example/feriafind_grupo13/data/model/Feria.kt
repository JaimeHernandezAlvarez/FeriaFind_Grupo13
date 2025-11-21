package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName
data class Feria(
    @SerializedName("idFeria")
    val id: String,
    @SerializedName("nombreFeria")
    val nombre: String,
    @SerializedName("ubicacion")
    val direccion: String,
    val latitud: Double = -33.4489, // Santiago Centro por defecto
    val longitud: Double = -70.6693
)