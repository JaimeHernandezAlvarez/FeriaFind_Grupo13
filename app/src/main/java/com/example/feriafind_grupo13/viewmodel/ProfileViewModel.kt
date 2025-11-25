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


class ProfileViewModel(
    private val repository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
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
                _uiState.update { it.copy(isLoading = false, error = "Sesión no encontrada. Por favor reinicia.") }
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
    fun onNombreChange(nuevoNombre: String) { _uiState.update { it.copy(nombre = nuevoNombre) }}
    fun onDescripcionChange(nuevaDescripcion: String) { _uiState.update { it.copy(descripcion = nuevaDescripcion) } }
    fun onHorarioChange(nuevoHorario: String) { _uiState.update { it.copy(horario = nuevoHorario) } }
    fun onFotoChange(nuevaUri: Uri?) { _uiState.update { it.copy(fotoUri = nuevaUri) } }

    fun guardarCambios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Mostrar carga

            val currentState = _uiState.value

            // Validamos que tengamos un email en sesión
            val email = repository.getLoggedInUserEmail().firstOrNull()
            if (email == null) {
                _uiState.update { it.copy(isLoading = false, error = "Error de sesión: no se encontró email") }
                return@launch
            }

            // Obtenemos el usuario base de la BD local para preservar datos sensibles (como password local)
            val userResult = repository.getUserProfile(email)
            if (userResult.isFailure) {
                _uiState.update { it.copy(isLoading = false, error = "Usuario no encontrado localmente") }
                return@launch
            }

            val originalUser = userResult.getOrNull()!!

            // Creamos el objeto actualizado combinando el original con los cambios de la UI
            val userToUpdate = originalUser.copy(
                nombre = currentState.nombre,
                descripcion = currentState.descripcion,
                horario = currentState.horario,
                fotoUri = currentState.fotoUri?.toString()
            )

            // Enviamos al repositorio para que sincronice nube y local
            repository.updateUserProfile(userToUpdate)
                .onSuccess {
                    originalState = _uiState.value // Actualizamos el estado base
                    _uiState.update { it.copy(isLoading = false, error = null) } // Quitamos carga
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al guardar: ${e.message}") }
                }
        }
    }

    fun restaurarValores() {
        _uiState.value = originalState
    }

    // --- LÓGICA DE BORRAR (DELETE) ---
    fun eliminarCuenta(onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val email = _uiState.value.correo

            if (email.isBlank()) {
                _uiState.update { it.copy(isLoading = false, error = "No hay email asociado para eliminar") }
                return@launch
            }

            // Llamamos a la función deleteUser del repositorio
            // Esta función se encarga de: Buscar ID remoto -> Borrar en API -> Borrar local -> Limpiar sesión
            repository.deleteUser(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    onSuccess() // Callback para navegar fuera (ej. al Login)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = "Error al eliminar: ${e.message}") }
                }
        }
    }
}
