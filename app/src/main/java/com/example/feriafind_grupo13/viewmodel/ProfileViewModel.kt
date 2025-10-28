package com.example.feriafind_grupo13.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 1. AHORA PIDE EL REPOSITORIO
class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Variable para guardar el estado original (para el botón 'Restaurar')
    private var originalState = ProfileUiState()

    init {
        // 2. Carga los datos del usuario al iniciar
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Obtenemos el email guardado en DataStore
            val email = repository.getLoggedInUserEmail().firstOrNull()
            if (email == null) {
                _uiState.update { it.copy(isLoading = false, error = "No se pudo encontrar la sesión") }
                return@launch
            }

            // Con el email, buscamos el perfil en Room (SQLite)
            repository.getUserProfile(email).onSuccess { userEntity ->
                val loadedState = ProfileUiState(
                    isLoading = false,
                    id = userEntity.id,
                    nombre = userEntity.nombre,
                    descripcion = userEntity.descripcion ?: "",
                    correo = userEntity.email,
                    horario = userEntity.horario ?: "",
                    // Convertimos el String de la BD de nuevo a Uri
                    fotoUri = userEntity.fotoUri?.toUri()
                )
                _uiState.value = loadedState
                originalState = loadedState // Guardamos el estado original

            }.onFailure {
                _uiState.update { it.copy(isLoading = false, error = it.error) }
            }
        }
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
        viewModelScope.launch {
            val currentState = _uiState.value

            // 1. Creamos la entidad UserEntity con los datos actuales
            // (Necesitamos el 'password', pero no lo tenemos. Lo ideal
            // sería obtener la entidad original y solo modificar campos).

            // --- Mejor enfoque: Obtener la entidad original ---
            val email = repository.getLoggedInUserEmail().firstOrNull() ?: return@launch
            val originalUserResult = repository.getUserProfile(email)

            if (originalUserResult.isFailure) {
                _uiState.update { it.copy(error = "Error al guardar: Usuario no encontrado") }
                return@launch
            }

            val userToUpdate = originalUserResult.getOrNull()!!

            // 2. Actualizamos la entidad con los datos del UiState
            val updatedEntity = userToUpdate.copy(
                nombre = currentState.nombre,
                descripcion = currentState.descripcion,
                horario = currentState.horario,
                // Convertimos la Uri a String para guardarla
                fotoUri = currentState.fotoUri?.toString()
            )

            // 3. Llamamos al repositorio para actualizar
            repository.updateUserProfile(updatedEntity).onSuccess {
                println("Guardando cambios: $updatedEntity")
                // Actualizamos el 'originalState' con los nuevos datos guardados
                originalState = _uiState.value
            }.onFailure {
                _uiState.update { it.copy(error = "Error al guardar") }
            }
        }
    }

    fun restaurarValores() {
        _uiState.value = originalState
    }
}