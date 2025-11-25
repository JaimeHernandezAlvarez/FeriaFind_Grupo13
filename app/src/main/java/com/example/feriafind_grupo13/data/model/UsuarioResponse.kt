package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName

// Estructura para leer la lista de usuarios desde Spring Boot (HATEOAS)
data class UsuarioResponse(
    @SerializedName("_embedded")
    val embedded: EmbeddedUsuario?
)

data class EmbeddedUsuario(
    // "usuarioList" es el nombre por defecto que Spring Data REST le da a la colección
    @SerializedName("usuarioList")
    val usuarios: List<Usuario>
)