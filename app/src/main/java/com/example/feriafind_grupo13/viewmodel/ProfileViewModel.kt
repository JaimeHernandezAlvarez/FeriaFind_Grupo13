package com.example.feriafind_grupo13.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feriafind_grupo13.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    private var originalState = ProfileUiState()

    init {
        loadUserProfile()
    }
    private fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Obtenemos el email guardado en DataStore
            val email = repository.getLoggedInUserEmail().firstOrNull()
            if (email.isNullOrBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión inválida") }
                return@launch
            }
            // Con el email, buscamos el perfil en Room (SQLite)
            repository.getUserProfile(email).onSuccess { userEntity ->
                // Convertir String a Uri de forma segura
                val uri = if (!userEntity.fotoUri.isNullOrBlank()) Uri.parse(userEntity.fotoUri) else null

                val loadedState = ProfileUiState(
                    isLoading = false,
                    id = userEntity.id,
                    nombre = userEntity.nombre,
                    descripcion = userEntity.descripcion ?: "",
                    correo = userEntity.email,
                    horario = userEntity.horario ?: "",
                    fotoUri = uri
                )
                _uiState.value = loadedState
                originalState = loadedState // Guardamos el estado original
            }.onFailure {
                _uiState.update { state -> state.copy(isLoading = false, error = "Error cargando perfil") }
            }
        }
    }
    // Setters
    fun onNombreChange(nuevoNombre: String) { _uiState.update { it.copy(nombre = nuevoNombre) }}
    fun onDescripcionChange(nuevaDescripcion: String) { _uiState.update { it.copy(descripcion = nuevaDescripcion) } }
    fun onHorarioChange(nuevoHorario: String) { _uiState.update { it.copy(horario = nuevoHorario) } }
    fun onFotoChange(nuevaUri: Uri?) { _uiState.update { it.copy(fotoUri = nuevaUri) } }

    fun guardarCambios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Mostrar carga

            val current = _uiState.value
            if (current.correo.isBlank()) return@launch

            val resultLocal = repository.getUserProfile(current.correo)
            if (resultLocal.isFailure) {
                _uiState.update { it.copy(isLoading = false, error = "Error local") }
                return@launch
            }
            val baseUser = resultLocal.getOrNull()!!

            val userToUpdate = baseUser.copy(
                nombre = current.nombre,
                descripcion = current.descripcion,
                horario = current.horario,
                fotoUri = current.fotoUri?.toString() ?: ""
            )
            repository.updateUserProfile(userToUpdate)
                .onSuccess {
                    originalState = current.copy(isLoading = false, error = null)
                    _uiState.value = originalState
                    _uiState.update { it.copy(error = "¡Guardado correctamente!") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al guardar: ${e.message}") }
                }
        }
    }
    fun restaurarValores() {
        _uiState.value = originalState
    }
    // --- LÓGICA DE BORRAR ---
    fun eliminarCuenta(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            repository.deleteUser(_uiState.value.correo)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al eliminar: ${e.message}") }
                }
        }
    }
}