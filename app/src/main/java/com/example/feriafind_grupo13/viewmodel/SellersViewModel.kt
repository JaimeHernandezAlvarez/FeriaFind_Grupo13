package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.local.favorites.FavoriteEntity
import com.example.feriafind_grupo13.data.repository.VendedorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class SellersViewModel(private val favoriteDao: FavoriteDao) : ViewModel() {

    // 1. Instanciamos el Repositorio que creamos antes
    private val repository = VendedorRepository()
    private val _uiState = MutableStateFlow(SellersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 2. En lugar de crear una lista manual, llamamos a la API al iniciar
        fetchVendedores()
        observeFavorites()
    }
    // Observa la base de datos en tiempo real: si agregas un fav, la UI se entera sola
    private fun observeFavorites() {
        viewModelScope.launch {
            favoriteDao.getAllFavorites().collect { listaFavoritos ->
                val ids = listaFavoritos.map { it.vendedorId }.toSet()
                _uiState.update { it.copy(favoritos = ids) }
            }
        }
    }
    private fun fetchVendedores() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val lista = repository.getVendedores()
                _uiState.update {
                    it.copy(todosLosVendedores = lista, vendedoresMostrados = lista, isLoading = false)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        val todos = _uiState.value.todosLosVendedores
        val filtrados = if (query.isBlank()) todos else todos.filter {
            it.nombre.lowercase().contains(query.lowercase())
        }
        _uiState.update { it.copy(vendedoresMostrados = filtrados) }
    }

    // Guarda o borra en la BD real
    fun onFavoritoClick(vendedorId: String) {
        viewModelScope.launch {
            if (favoriteDao.isFavorite(vendedorId)) {
                favoriteDao.removeFavorite(vendedorId)
            } else {
                favoriteDao.addFavorite(FavoriteEntity(vendedorId))
            }
        }
    }
}