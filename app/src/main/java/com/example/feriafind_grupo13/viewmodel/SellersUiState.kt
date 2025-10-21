package com.example.feriafind_grupo13.viewmodel

import com.example.feriafind_grupo13.data.model.Vendedor

// Estado de la UI para la lista de vendedores
data class SellersUiState(
    val todosLosVendedores: List<Vendedor> = emptyList(),
    val vendedoresMostrados: List<Vendedor> = emptyList(),
    val searchQuery: String = "",
    val favoritos: Set<String> = emptySet()
)
