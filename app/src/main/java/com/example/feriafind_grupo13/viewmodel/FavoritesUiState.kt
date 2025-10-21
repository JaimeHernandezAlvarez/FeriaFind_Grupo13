package com.example.feriafind_grupo13.viewmodel

import com.example.feriafind_grupo13.data.model.Vendedor

// Estado de la UI para la pantalla de favoritos
data class FavoritesUiState(
    val vendedoresFavoritos: List<Vendedor> = emptyList()
)
