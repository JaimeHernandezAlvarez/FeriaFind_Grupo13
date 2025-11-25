package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.local.favorites.FavoriteEntity
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.viewmodel.FavoritesViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    // 1. Mocks de las dependencias
    private val favoriteDao: FavoriteDao = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)

    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // 2. Simulamos el email del usuario logueado
        val emailPrueba = "test@duoc.cl"
        every { userRepository.getLoggedInUserEmail() } returns flowOf(emailPrueba)

        // 3. Simulamos que el DAO devuelve favoritos para ESTE usuario
        every { favoriteDao.getUserFavorites(emailPrueba) } returns flowOf(
            listOf(FavoriteEntity("101", emailPrueba))
        )

        // 4. Inicializamos el ViewModel con AMBOS mocks
        viewModel = FavoritesViewModel(favoriteDao, userRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarFavoritos obtiene datos del DAO usando el email`() = runTest {
        // THEN: Verificamos que se llamó a getUserFavorites con el email correcto
        // Nota: El repositorio de Vendedores fallará silenciosamente (try-catch) porque no está mockeado,
        // pero lo importante aquí es probar la interacción con la base de datos local.

        verify { favoriteDao.getUserFavorites("test@duoc.cl") }
    }

    @Test
    fun `click en favorito llama a removeFavorite con ID y Email`() = runTest {
        // GIVEN
        val id = "101"
        val email = "test@duoc.cl"

        // WHEN
        viewModel.onFavoritoClick(id)

        // THEN: Verificamos que se elimina usando el ID y el Email del usuario
        coVerify { favoriteDao.removeFavorite(id, email) }
    }
}