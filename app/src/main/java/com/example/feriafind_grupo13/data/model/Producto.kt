package com.example.feriafind_grupo13.data.model

//Representa un producto que un Vendedor ofrece.

data class Producto(
    val id: String,
    val nombre: String,
    val precio: Double, // Usar Double para precios
    val categoria: String, // Ej: "Verduras", "Frutas"
    val imagenUrl: String,
    val idVendedor: String // Clave foránea para saber quién lo vende
)