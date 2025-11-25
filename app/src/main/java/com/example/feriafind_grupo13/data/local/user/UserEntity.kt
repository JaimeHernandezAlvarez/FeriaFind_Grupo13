package com.example.feriafind_grupo13.data.local.user

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val nombre: String,
    val email: String,
    val password: String,
    val descripcion: String? = null,
    val horario: String? = null,
    val fotoUri: String? = null // Room no puede guardar 'Uri', lo guardamos como String
)