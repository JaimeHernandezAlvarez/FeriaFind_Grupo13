package com.example.feriafind_grupo13.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.repository.VendedorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class SellersViewModel : ViewModel() {

    // 1. Instanciamos el Repositorio que creamos antes
    private val repository = VendedorRepository()

    private val _uiState = MutableStateFlow(SellersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 2. En lugar de crear una lista manual, llamamos a la API al iniciar
        fetchVendedores()
    }
    private fun fetchVendedores() {
        // Iniciamos carga
        _uiState.update { it.copy(isLoading = true) }

        viewModelScope.launch {
            try {
                Log.d("API_DEBUG", "Llamando a API...")
                val lista = repository.getVendedores()
                Log.d("API_DEBUG", "¡Éxito! Recibidos: ${lista.size}")

                _uiState.update {
                    it.copy(
                        todosLosVendedores = lista,
                        vendedoresMostrados = lista,
                        isLoading = false, // Apagamos carga
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                Log.e("API_DEBUG", "Error: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false, // Apagamos carga aunque fallé
                        errorMessage = "Fallo conexión: ${e.message}"
                    )
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filtrarVendedores()
    }

    private fun filtrarVendedores() {
        val query = _uiState.value.searchQuery
        val todos = _uiState.value.todosLosVendedores

        val filtrados = if (query.isBlank()) {
            todos
        } else {
            todos.filter { vendedor ->
                // Ajusta 'nombreVendedor' según tu modelo real
                vendedor.nombre.lowercase(Locale.getDefault())
                    .contains(query.lowercase(Locale.getDefault()))
            }
        }
        _uiState.update { it.copy(vendedoresMostrados = filtrados) }
    }

    fun onFavoritoClick(vendedorId: String) {
        val favoritosActuales = _uiState.value.favoritos.toMutableSet()
        if (favoritosActuales.contains(vendedorId)) {
            favoritosActuales.remove(vendedorId)
        } else {
            favoritosActuales.add(vendedorId)
        }
        _uiState.update { it.copy(favoritos = favoritosActuales) }
    }
}