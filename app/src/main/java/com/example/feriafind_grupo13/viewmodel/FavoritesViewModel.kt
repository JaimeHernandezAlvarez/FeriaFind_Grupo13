package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feriafind_grupo13.data.model.Vendedor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class FavoritesViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    // Este ViewModel necesitará una forma de saber cuáles son los vendedores
    // y cuáles son los favoritos. Por ahora, lo simulamos.
    private val todosLosVendedores = listOf(
        Vendedor("101", "user1", "Juan Pérez", "Vendedor desde 2017", "9:00 - 14:00", ""),
        Vendedor("102", "user2", "Pedro Soto", "Vendedor desde 2020", "12:00 - 15:30", ""),
        Vendedor("103", "user3", "Luis Rojas", "Vendedor desde 2023", "8:30 - 13:00", "")
    )
    private var favoritosIds = mutableSetOf("101")

    init {
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        val favoritos = todosLosVendedores.filter { it.id in favoritosIds }
        _uiState.update { it.copy(vendedoresFavoritos = favoritos) }
    }

    fun onFavoritoClick(vendedorId: String) {
        if (favoritosIds.contains(vendedorId)) {
            favoritosIds.remove(vendedorId)
        } else {
            favoritosIds.add(vendedorId)
        }
        // Recargamos la lista para que la UI se actualice
        cargarFavoritos()
    }
}