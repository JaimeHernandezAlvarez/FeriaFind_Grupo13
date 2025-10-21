package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feriafind_grupo13.data.model.Producto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class ProductsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Cargar productos de ejemplo
        val productos = listOf(
            Producto("1", "Tomates", 1200.0, "Verduras", "", "101"),
            Producto("2", "Naranjas", 1100.0, "Frutas", "", "102"),
            Producto("3", "Lechugas", 500.0, "Verduras", "", "103")
        )
        _uiState.value = ProductsUiState(
            todosLosProductos = productos,
            productosMostrados = productos
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filtrarProductos()
    }

    private fun filtrarProductos() {
        val query = _uiState.value.searchQuery
        val productosFiltrados = if (query.isBlank()) {
            _uiState.value.todosLosProductos
        } else {
            _uiState.value.todosLosProductos.filter {
                it.nombre.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }
        }
        _uiState.update { it.copy(productosMostrados = productosFiltrados) }
    }
}
