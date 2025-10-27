package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feriafind_grupo13.data.repository.UserRepository

/**
 * Esta clase es la "receta" que le dice a Android cómo crear
 * un AuthViewModel, ya que este necesita un UserRepository.
 */
class AuthViewModelFactory(
    private val repository: UserRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica si la clase que se pide es AuthViewModel
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            // Si lo es, la crea pasándole el repositorio y la devuelve
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(repository) as T
        }
        // Si se pide cualquier otro ViewModel, lanza un error
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}