package com.example.feriafind_grupo13.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private var originalState = ProfileUiState()

    init {
        // Simula la carga de datos inicial
        _uiState.update {
            it.copy(
                nombre = "Juan Perez",
                descripcion = "Vendedor desde 2017",
                correo = "juanito.15@gmail.com",
                horario = "9:00 - 14:00"
            )
        }
        originalState = _uiState.value
    }

    fun onNombreChange(nuevoNombre: String) {
        _uiState.update { it.copy(nombre = nuevoNombre) }
    }

    fun onDescripcionChange(nuevaDescripcion: String) {
        _uiState.update { it.copy(descripcion = nuevaDescripcion) }
    }

    fun onHorarioChange(nuevoHorario: String) {
        _uiState.update { it.copy(horario = nuevoHorario) }
    }

    fun onFotoChange(nuevaUri: Uri?) {
        _uiState.update { it.copy(fotoUri = nuevaUri) }
    }

    fun guardarCambios() {
        // Aquí iría la lógica para guardar los datos en una base de datos o API
        println("Guardando cambios: ${_uiState.value}")
        originalState = _uiState.value
    }

    fun restaurarValores() {
        _uiState.value = originalState
    }
}
