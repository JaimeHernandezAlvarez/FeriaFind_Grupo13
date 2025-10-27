package com.example.feriafind_grupo13.data.local.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Añadimos 'nombre' y un índice 'unique' para el email
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)] // Evita emails duplicados
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val nombre: String, // Campo añadido
    val email: String,
    val password: String
)