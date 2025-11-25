package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.data.repository.ProductRepository
import com.example.feriafind_grupo13.viewmodel.ProductsViewModel
import io.mockk.coEvery
import io.mockk.mockk
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

    // 1. Creamos el repositorio falso
    private val repository: ProductRepository = mockk()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        // 2. Le decimos al repositorio qué devolver (3 productos de prueba)
        val listaPrueba = listOf(
            Producto(1, "Tomates", 1000.0, 1, "", 10),
            Producto(2, "Papas", 500.0, 1, "", 10),
            Producto(3, "Lechuga", 800.0, 1, "", 10)
        )
        coEvery { repository.getProductos() } returns listaPrueba

        // 3. Iniciamos el ViewModel con el repositorio falso
        viewModel = ProductsViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carga inicial muestra todos los productos`() {
        // THEN: Ahora sí esperamos 3 productos, porque eso fue lo que configuramos en el mock
        val productos = viewModel.uiState.value.productosMostrados
        assertEquals(3, productos.size)
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
        viewModel.onSearchQueryChange("Tomates")

        // WHEN
        viewModel.onSearchQueryChange("")

        // THEN
        val filtrados = viewModel.uiState.value.productosMostrados
        assertEquals(3, filtrados.size)
    }
}