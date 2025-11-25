package com.example.feriafind_grupo13.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.repository.ProductRepository
import com.example.feriafind_grupo13.data.repository.UserRepository

/**
 * Esta clase es la "Fábrica" que enseña a Android cómo crear tus ViewModels
 * inyectándoles las dependencias necesarias (Repositorio o DAO).
 */
class AppViewModelFactory(
    private val userRepository: UserRepository? = null,
    private val favoriteDao: FavoriteDao? = null
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            // 1. AuthViewModel: Requiere UserRepository
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository!!) as T
            }
            // 2. ProfileViewModel: Requiere UserRepository
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository!!) as T
            }
            // 3. SellersViewModel: Requiere FavoriteDao Y UserRepository
            modelClass.isAssignableFrom(SellersViewModel::class.java) -> {
                SellersViewModel(favoriteDao!!, userRepository!!) as T
            }
            // 4. FavoritesViewModel: Requiere FavoriteDao Y UserRepository
            // CORRECCIÓN AQUÍ: Le pasamos AMBOS parámetros
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(favoriteDao!!, userRepository!!) as T
            }
            // 5. ProductsViewModel: Requiere ProductRepository
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> {
                ProductsViewModel(ProductRepository()) as T
            }
            // 6. ViewModels simples
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel() as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel() as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}