package com.example.feriafind_grupo13.data.model

// Representa una ubicación física de una feria, con sus coordenadas para el mapa.

data class Feria(
    val id: String,
    val nombre: String,
    val direccion: String,
    val latitud: Double,
    val longitud: Double
)