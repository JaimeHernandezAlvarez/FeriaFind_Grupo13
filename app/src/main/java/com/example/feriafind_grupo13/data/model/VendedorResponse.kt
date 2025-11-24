package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName

// Estructura para leer el JSON formato HATEOAS de tu imagen
data class VendedorResponse(
    @SerializedName("_embedded")
    val embedded: EmbeddedData?
)

data class EmbeddedData(
    // AQUÍ ESTÁ LA CLAVE: Coincide con tu imagen "vendedorList"
    @SerializedName("vendedorList")
    val vendedores: List<Vendedor>
)