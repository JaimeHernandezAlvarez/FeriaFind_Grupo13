package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feriafind_grupo13.data.model.Vendedor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Locale

class SellersViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SellersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val vendedores = listOf(
            Vendedor("101", "user1", "Juan Pérez", "Vendedor desde 2017", "9:00 - 14:00", ""),
            Vendedor("102", "user2", "Pedro Soto", "Vendedor desde 2020", "12:00 - 15:30", ""),
            Vendedor("103", "user3", "Luis Rojas", "Vendedor desde 2023", "8:30 - 13:00", "")
        )
        _uiState.value = SellersUiState(
            todosLosVendedores = vendedores,
            vendedoresMostrados = vendedores,
            favoritos = setOf("101") // Juan Pérez es favorito por defecto
        )
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        filtrarVendedores()
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

    private fun filtrarVendedores() {
        val query = _uiState.value.searchQuery
        val vendedoresFiltrados = if (query.isBlank()) {
            _uiState.value.todosLosVendedores
        } else {
            _uiState.value.todosLosVendedores.filter {
                it.nombre.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault()))
            }
        }
        _uiState.update { it.copy(vendedoresMostrados = vendedoresFiltrados) }
    }
}