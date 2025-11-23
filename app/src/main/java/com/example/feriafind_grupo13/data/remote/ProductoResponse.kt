package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Producto
import com.google.gson.annotations.SerializedName

data class ProductoResponse(
    @SerializedName("_embedded")
    val embedded: EmbeddedProducts?
)

data class EmbeddedProducts(
    // Basado en la lógica de Spring HATEOAS, si la clase es "Producto", la lista suele ser "productoList"
    @SerializedName("productoList")
    val productos: List<Producto>
)