package com.example.feriafind_grupo13.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class ProductsViewModel(private val repository: ProductRepository = ProductRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetchProductos()
    }
    private fun fetchProductos() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                Log.d("API_PRODUCTOS", "Cargando productos...")
                val lista = repository.getProductos()
                Log.d("API_PRODUCTOS", "Productos recibidos: ${lista.size}")
                    _uiState.update {
                        it.copy(todosLosProductos = lista, productosMostrados = lista, isLoading = false, errorMessage = null)
                }
            } catch (e: Exception) { Log.e("API_PRODUCTOS", "Error: ${e.message}")
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Error al cargar: ${e.message}") }
            }
        }
    }
    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filtrarProductos()
    }
    private fun filtrarProductos() {
        val query = _uiState.value.searchQuery
        val todos = _uiState.value.todosLosProductos
        val filtrados = if (query.isBlank()) { todos } else { todos.filter {
                it.nombre.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())) }
        }
        _uiState.update { it.copy(productosMostrados = filtrados) }
    }
    // --- CRUD ---
    fun crearProducto(producto: Producto) {
        viewModelScope.launch {
            repository.createProducto(producto)
            fetchProductos() // Recargar lista
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.updateProducto(producto)
            fetchProductos()
        }
    }

    fun eliminarProducto(id: Int) {
        viewModelScope.launch {
            repository.deleteProducto(id)
            fetchProductos()
        }
    }
}