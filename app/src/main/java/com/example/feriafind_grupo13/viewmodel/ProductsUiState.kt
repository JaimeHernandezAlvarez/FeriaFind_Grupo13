package com.example.feriafind_grupo13.viewmodel

import com.example.feriafind_grupo13.data.model.Producto

// Estado de la UI para la lista de productos
data class ProductsUiState(
    val todosLosProductos: List<Producto> = emptyList(),
    val productosMostrados: List<Producto> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = true, // Para el círculo de carga
    val errorMessage: String? = null // Para errores rojos
)

