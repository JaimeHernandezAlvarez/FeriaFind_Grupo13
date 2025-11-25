package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.repository.VendedorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(private val favoriteDao: FavoriteDao) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()
    private val vendedorRepository = VendedorRepository()

    init {
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        viewModelScope.launch {
            // 1. Obtenemos la lista de IDs guardados en el celular
            favoriteDao.getAllFavorites().collect { entities ->
                val idsFavoritos = entities.map { it.vendedorId }.toSet()

                try {
                    // 2. Obtenemos los vendedores de la API
                    val todos = vendedorRepository.getVendedores()

                    // 3. Filtramos para mostrar solo los que están en mis favoritos
                    val vendedoresFav = todos.filter { it.id in idsFavoritos }

                    _uiState.update { it.copy(vendedoresFavoritos = vendedoresFav) }
                } catch (e: Exception) {
                    // Manejo de error silencioso o mostrar en UI
                }
            }
        }
    }

    fun onFavoritoClick(vendedorId: String) {
        viewModelScope.launch {
            favoriteDao.removeFavorite(vendedorId)
        }
    }
}