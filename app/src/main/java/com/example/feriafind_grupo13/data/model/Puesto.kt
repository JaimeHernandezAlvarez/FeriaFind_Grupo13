package com.example.feriafind_grupo13.data.model

// Modelo de relación: Indica qué vendedor está en qué feria y en qué día. Esto es clave para la funcionalidad del mapa y los filtros.

data class Puesto(
    val id: String,
    val idVendedor: String, // Clave foránea de Vendedor
    val idFeria: String,    // Clave foránea de Feria
    val diaSemana: String  // Ej: "Domingo", "Martes"
)