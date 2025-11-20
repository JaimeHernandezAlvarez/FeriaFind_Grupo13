package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.navigation.NavigationEvent
import com.example.feriafind_grupo13.navigation.Screen
import com.example.feriafind_grupo13.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    private lateinit var viewModel: MainViewModel
    // Usamos UnconfinedTestDispatcher para que los eventos se emitan inmediatamente
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = MainViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `MapsTo emite evento NavigateTo correcto`() = runTest {
        // GIVEN: Una lista para capturar los eventos que emita el ViewModel
        val eventosCapturados = mutableListOf<NavigationEvent>()

        // Iniciamos una corrutina en segundo plano para "escuchar" los eventos
        backgroundScope.launch(testDispatcher) {
            viewModel.navigationEvents.collect { eventosCapturados.add(it) }
        }

        // WHEN: Llamamos a la función de navegar
        val destino = Screen.Login
        viewModel.navigateTo(destino)

        // THEN: Verificamos que se recibió el evento correcto
        assertEquals(1, eventosCapturados.size)
        val evento = eventosCapturados.first()

        assertTrue(evento is NavigationEvent.NavigateTo)
        assertEquals(destino, (evento as NavigationEvent.NavigateTo).route)
    }

    @Test
    fun `MapsBack emite evento PopBackStack`() = runTest {
        // GIVEN
        val eventosCapturados = mutableListOf<NavigationEvent>()
        backgroundScope.launch(testDispatcher) {
            viewModel.navigationEvents.collect { eventosCapturados.add(it) }
        }

        // WHEN
        viewModel.navigateBack()

        // THEN
        assertEquals(1, eventosCapturados.size)
        assertTrue(eventosCapturados.first() is NavigationEvent.PopBackStack)
    }

    @Test
    fun `MapsUp emite evento NavigateUp`() = runTest {
        // GIVEN
        val eventosCapturados = mutableListOf<NavigationEvent>()
        backgroundScope.launch(testDispatcher) {
            viewModel.navigationEvents.collect { eventosCapturados.add(it) }
        }

        // WHEN
        viewModel.navigateUp()

        // THEN
        assertEquals(1, eventosCapturados.size)
        assertTrue(eventosCapturados.first() is NavigationEvent.NavigateUp)
    }
}