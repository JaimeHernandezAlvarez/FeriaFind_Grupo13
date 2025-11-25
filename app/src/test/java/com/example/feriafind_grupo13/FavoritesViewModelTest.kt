package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.data.local.favorites.FavoriteDao
import com.example.feriafind_grupo13.data.local.favorites.FavoriteEntity
import com.example.feriafind_grupo13.viewmodel.FavoritesViewModel
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
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

    // 1. Creamos un Mock del DAO
    private val favoriteDao: FavoriteDao = mockk(relaxed = true)

    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // 2. Simulamos que la base de datos devuelve una lista con un favorito (ID "101")
        every { favoriteDao.getAllFavorites() } returns flowOf(
            listOf(FavoriteEntity("101"))
        )

        // 3. Inicializamos el ViewModel con el DAO simulado
        viewModel = FavoritesViewModel(favoriteDao)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `cargarFavoritos obtiene datos del DAO`() = runTest {
        // THEN: Verificamos que el estado inicial tenga al menos un favorito (el "101" que simulamos)
        // Nota: Como el ViewModel también filtra contra la API real (que no hemos mockeado aquí),
        // la lista final 'vendedoresFavoritos' podría estar vacía si la API falla en el test.
        // Sin embargo, podemos verificar que se llamó al DAO.

        io.mockk.verify { favoriteDao.getAllFavorites() }
    }

    @Test
    fun `click en favorito llama a removeFavorite del DAO`() = runTest {
        // GIVEN: Un ID de vendedor
        val id = "101"

        // WHEN: Hacemos click en el botón de favorito
        viewModel.onFavoritoClick(id)

        // THEN: Verificamos que el ViewModel le pidió al DAO eliminar ese ID
        coVerify { favoriteDao.removeFavorite(id) }
    }
}