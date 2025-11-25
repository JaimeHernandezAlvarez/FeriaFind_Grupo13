package com.example.feriafind_grupo13.data.local.favorites

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["vendedorId", "userEmail"])
data class FavoriteEntity(
    val vendedorId: String,
    val userEmail: String // Nuevo campo: Email del usuario dueño del favorito
)