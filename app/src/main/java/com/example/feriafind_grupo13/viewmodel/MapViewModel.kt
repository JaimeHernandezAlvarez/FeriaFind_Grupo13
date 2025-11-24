package com.example.feriafind_grupo13.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.model.Feria
import com.example.feriafind_grupo13.data.remote.WeatherRetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        cargarFerias()
        cargarClima()
    }

    // CORRECCIÓN: Usamos 'currentState' en lugar de 'it' para ser explícitos
    private fun cargarFerias() {
        _uiState.update { currentState ->
            currentState.copy(
                ferias = listOf(
                    Feria("f1", "Feria Parque de los Reyes", "Av. Presidente Balmaceda", -33.4293, -70.6633),
                    Feria("f2", "Feria Libre de Ñuñoa", "Calle Juan Moya", -33.4569, -70.5969),
                    Feria("f3", "Vega Central", "Davila Baeza 700", -33.4211, -70.6502)
                )
            )
        }
    }

    private fun cargarClima() {
        viewModelScope.launch {
            try {
                // Coordenadas de Santiago
                val latSantiago = -33.4489
                val lonSantiago = -70.6693

                val response = WeatherRetrofitInstance.api.getWeather(latSantiago, lonSantiago)
                val temp = response.currentWeather.temperature

                // Actualización segura
                _uiState.update { currentState ->
                    currentState.copy(climaInfo = "Santiago: $temp°C")
                }

            } catch (e: Exception) {
                _uiState.update { currentState ->
                    currentState.copy(climaInfo = "Clima no disponible")
                }
            }
        }
    }

    fun onDiaChange(dia: String) {
        _uiState.update { currentState -> currentState.copy(diaSeleccionado = dia) }
    }

    fun onRubroChange(rubro: String) {
        _uiState.update { currentState -> currentState.copy(rubroSeleccionado = rubro) }
    }
}