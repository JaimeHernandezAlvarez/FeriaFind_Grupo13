package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import com.example.feriafind_grupo13.data.model.Feria
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // Cargar las ferias iniciales (datos de ejemplo)
        _uiState.update {
            it.copy(
                ferias = listOf(
                    Feria("f1", "Feria de Recoleta", "Av. Recoleta 123", -33.4178, -70.6415),
                    Feria("f2", "Feria de Independencia", "Av. Independencia 456", -33.4244, -70.6504),
                    Feria("f3", "Feria de Santiago", "Calle Santiago 789", -33.4449, -70.6569)
                )
            )
        }
    }

    fun onDiaChange(dia: String) {
        _uiState.update { it.copy(diaSeleccionado = dia) }
        // Aquí podrías agregar lógica para filtrar las ferias según el día
    }

    fun onRubroChange(rubro: String) {
        _uiState.update { it.copy(rubroSeleccionado = rubro) }
        // Aquí podrías agregar lógica para filtrar las ferias según el rubro
    }
}
