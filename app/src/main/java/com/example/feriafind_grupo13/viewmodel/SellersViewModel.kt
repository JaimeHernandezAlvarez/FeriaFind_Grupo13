package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.local.favorites.FavoriteEntity
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.data.repository.VendedorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

class SellersViewModel(
    private val favoriteDao: FavoriteDao,
    private val userRepository: UserRepository // Inyectamos el repo de usuario
) : ViewModel() {

    // 1. Instanciamos el Repositorio que creamos antes
    private val repository = VendedorRepository()
    private val _uiState = MutableStateFlow(SellersUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // 2. En lugar de crear una lista manual, llamamos a la API al iniciar
        fetchVendedores()
        observeFavorites()
    }

    // Observa la base de datos en tiempo real: FILTRADO POR USUARIO
    private fun observeFavorites() {
        viewModelScope.launch {
            // Obtenemos el email del usuario actual
            val email = userRepository.getLoggedInUserEmail().firstOrNull()

            if (email != null) {
                // Usamos la función getUserFavorites(email) del DAO
                favoriteDao.getUserFavorites(email).collect { listaFavoritos ->
                    val ids = listaFavoritos.map { it.vendedorId }.toSet()
                    _uiState.update { it.copy(favoritos = ids) }
                }
            } else {
                // Si no hay usuario, limpiamos favoritos
                _uiState.update { it.copy(favoritos = emptySet()) }
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
            it.nombre.lowercase(Locale.getDefault())
                .contains(query.lowercase(Locale.getDefault()))
        }
        _uiState.update { it.copy(vendedoresMostrados = filtrados) }
    }

    // Guarda o borra en la BD real, asociado al USUARIO ACTUAL
    fun onFavoritoClick(vendedorId: String) {
        viewModelScope.launch {
            // Obtenemos el email para guardar el favorito correctamente
            val email = userRepository.getLoggedInUserEmail().firstOrNull() ?: return@launch

            // Usamos isFavorite con (vendedorId, userEmail)
            if (favoriteDao.isFavorite(vendedorId, email)) {
                favoriteDao.removeFavorite(vendedorId, email)
            } else {
                // Creamos la entidad con ambos datos
                favoriteDao.addFavorite(FavoriteEntity(vendedorId, email))
            }
        }
    }
}