package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.viewmodel.ProductsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductsViewModelTest {

    private lateinit var viewModel: ProductsViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ProductsViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carga inicial muestra todos los productos`() {
        // THEN
        // Verificamos que la lista no esté vacía al inicio (según tus datos de prueba)
        val productos = viewModel.uiState.value.productosMostrados
        assertEquals(3, productos.size) // Tienes 3 productos hardcodeados en el ViewModel
    }

    @Test
    fun `busqueda filtra productos correctamente`() {
        // GIVEN
        val query = "Tomates"

        // WHEN
        viewModel.onSearchQueryChange(query)

        // THEN
        val filtrados = viewModel.uiState.value.productosMostrados
        assertEquals(1, filtrados.size)
        assertEquals("Tomates", filtrados.first().nombre)
    }

    @Test
    fun `busqueda vacia muestra todos los productos`() {
        // GIVEN
        viewModel.onSearchQueryChange("Tomates") // Primero filtramos

        // WHEN
        viewModel.onSearchQueryChange("") // Luego limpiamos

        // THEN
        val filtrados = viewModel.uiState.value.productosMostrados
        assertEquals(3, filtrados.size)
    }
}