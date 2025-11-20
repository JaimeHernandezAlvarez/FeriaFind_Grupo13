package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.viewmodel.FavoritesViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class FavoritesViewModelTest {

    private lateinit var viewModel: FavoritesViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = FavoritesViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carga inicial muestra favoritos por defecto`() {
        // En tu ViewModel, el ID "101" está hardcodeado como favorito inicial
        val favoritos = viewModel.uiState.value.vendedoresFavoritos
        assertEquals(1, favoritos.size)
        assertEquals("101", favoritos.first().id)
    }

    @Test
    fun `click en favorito existente lo elimina`() {
        // GIVEN: El 101 ya es favorito

        // WHEN: Hacemos click en 101
        viewModel.onFavoritoClick("101")

        // THEN: La lista debe estar vacía
        val favoritos = viewModel.uiState.value.vendedoresFavoritos
        assertTrue(favoritos.isEmpty())
    }

    @Test
    fun `click en nuevo vendedor lo agrega a favoritos`() {
        // GIVEN: El 102 NO es favorito

        // WHEN: Hacemos click en 102
        viewModel.onFavoritoClick("102")

        // THEN: La lista debe tener ahora 2 elementos (101 original + 102 nuevo)
        val favoritos = viewModel.uiState.value.vendedoresFavoritos
        assertEquals(2, favoritos.size)
        assertTrue(favoritos.any { it.id == "102" })
    }
}