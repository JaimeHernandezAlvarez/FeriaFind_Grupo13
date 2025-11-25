package com.example.feriafind_grupo13.data.local.favorites

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey
    val vendedorId: String // Guardamos el ID del vendedor favorito
)