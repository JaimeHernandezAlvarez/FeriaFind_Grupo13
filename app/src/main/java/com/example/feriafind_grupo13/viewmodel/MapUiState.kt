package com.example.feriafind_grupo13.viewmodel

import com.example.feriafind_grupo13.data.model.Feria

// Estado de la UI para la pantalla del mapa
data class MapUiState(
    val ferias: List<Feria> = emptyList(),
    val diaSeleccionado: String = "Domingo",
    val rubroSeleccionado: String = "Verduras",
    val climaInfo: String = "Cargando clima..."
)
