package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName

data class Categoria(
    @SerializedName("idCategoria")
    val id: String,
    @SerializedName("nombreCategoria")
    val nombre: String
)