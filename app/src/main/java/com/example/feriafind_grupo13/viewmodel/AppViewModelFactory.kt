package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.repository.ProductRepository
import com.example.feriafind_grupo13.data.repository.UserRepository

class AppViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val favoriteDao: FavoriteDao? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // 1. AuthViewModel
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository!!) as T
            }
            // 2. ProfileViewModel
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository!!) as T
            }
            // 3. SellersViewModel
            modelClass.isAssignableFrom(SellersViewModel::class.java) -> {
                SellersViewModel(favoriteDao!!, userRepository!!) as T
            }
            // 4. FavoritesViewModel
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(favoriteDao!!, userRepository!!) as T
            }
            // 5. ProductsViewModel
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> {
                ProductsViewModel(ProductRepository()) as T
            }

            // --- CAMBIO AQUÍ: MainViewModel ---
            // Ahora requiere el repositorio para restaurar la sesión (Token)
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository!!) as T
            }

            // 7. MapViewModel (Si este no tiene constructor, se queda así)
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}