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
            // Si piden AuthViewModel, le pasamos el Repositorio
            modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                AuthViewModel(userRepository!!) as T
            }
            // Si piden ProfileViewModel, le pasamos el Repositorio
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository!!) as T
            }
            // Si piden SellersViewModel, le pasamos el DAO de favoritos
            modelClass.isAssignableFrom(SellersViewModel::class.java) -> {
                SellersViewModel(favoriteDao!!) as T
            }
            // Si piden FavoritesViewModel, le pasamos el DAO de favoritos
            modelClass.isAssignableFrom(FavoritesViewModel::class.java) -> {
                FavoritesViewModel(favoriteDao!!) as T
            }
            // MainViewModel, MapViewModel y ProductsViewModel no tienen dependencias complejas
            // en este diseño, así que los creamos directo.
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel() as T
            }
            modelClass.isAssignableFrom(MapViewModel::class.java) -> {
                MapViewModel() as T
            }
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> {
                ProductsViewModel() as T
            }
            modelClass.isAssignableFrom(ProductsViewModel::class.java) -> {
                ProductsViewModel(ProductRepository()) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}