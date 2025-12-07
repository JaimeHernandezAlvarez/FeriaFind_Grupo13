package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope // Importante
import com.example.feriafind_grupo13.data.remote.UserRetrofitInstance
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// 1. Agregamos UserRepository al constructor
class MainViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents: SharedFlow<NavigationEvent> = _navigationEvents.asSharedFlow()

    // 2. Bloque INIT para restaurar la sesión al abrir la app
    init {
        viewModelScope.launch {
            // Escuchamos el DataStore por si hay un token guardado
            userRepository.getAuthToken.collect { token ->
                if (!token.isNullOrBlank()) {
                    // ¡Restauramos el token en Retrofit!
                    UserRetrofitInstance.setToken(token)
                }
            }
        }
    }

    // Navegación (Usando viewModelScope para seguridad de memoria)
    fun navigateTo(screen: Screen) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateTo(route = screen))
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.PopBackStack)
        }
    }

    fun navigateUp() {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateUp)
        }
    }
}