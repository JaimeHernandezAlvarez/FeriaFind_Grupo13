package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.data.repository.VendedorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. Aceptamos UserRepository en el constructor (esto soluciona el error de la Factory)
class FavoritesViewModel(
    private val favoriteDao: FavoriteDao,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()
    private val vendedorRepository = VendedorRepository()

    init {
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        viewModelScope.launch {
            // 2. Obtenemos el email del usuario actual
            val email = userRepository.getLoggedInUserEmail().firstOrNull() ?: return@launch

            // 3. Usamos la nueva función del DAO que filtra por email
            favoriteDao.getUserFavorites(email).collect { entities ->
                val idsFavoritos = entities.map { it.vendedorId }.toSet()

                try {
                    // Obtenemos todos los vendedores de la API para filtrar
                    val todos = vendedorRepository.getVendedores()

                    // Filtramos solo los que están en la lista de IDs favoritos
                    // Asegúrate de convertir tipos si es necesario (String vs Int)
                    val vendedoresFav = todos.filter { it.id in idsFavoritos }

                    _uiState.update { it.copy(vendedoresFavoritos = vendedoresFav) }
                } catch (e: Exception) {
                    // Manejo de error silencioso
                }
            }
        }
    }

    fun onFavoritoClick(vendedorId: String) {
        viewModelScope.launch {
            // Necesitamos el email para saber qué borrar
            val email = userRepository.getLoggedInUserEmail().firstOrNull() ?: return@launch

            // Llamamos al removeFavorite actualizado (pide ID y Email)
            favoriteDao.removeFavorite(vendedorId, email)
        }
    }
}