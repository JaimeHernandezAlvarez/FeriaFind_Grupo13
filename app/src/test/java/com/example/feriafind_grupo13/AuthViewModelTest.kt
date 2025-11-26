package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.data.local.user.UserEntity
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.viewmodel.AuthViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    // 1. Mock del Repositorio
    private val repository: UserRepository = mockk()

    // 2. ViewModel a probar
    private lateinit var viewModel: AuthViewModel

    // 3. Dispatcher para pruebas de corrutinas
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `iniciarSesion exitoso actualiza estado y navega`() = runTest {
        // GIVEN (Dado): Un escenario donde el usuario y contraseña son correctos
        val email = "test@duoc.cl"
        val pass = "12345678"

        // --- CORRECCIÓN AQUÍ ---
        // Actualizamos el objeto Fake para incluir los nuevos campos del Backend.
        // Usamos argumentos nombrados para evitar errores de orden.
        val fakeUser = UserEntity(
            id = 1,
            nombre = "Test User",
            email = email,
            password = pass, // Simula el hash guardado
            descripcion = "Descripción de prueba para el test", // Campo nuevo
            horario = "09:00 - 18:00", // Campo nuevo
            fotoUri = null // Campo nuevo
        )

        // Le enseñamos al Mock qué responder
        coEvery { repository.loginUser(email, pass) } returns Result.success(fakeUser)

        // WHEN (Cuando): La UI ingresa los datos y presiona login
        viewModel.onEmailChange(email)
        viewModel.onContrasenaChange(pass)
        viewModel.iniciarSesion()

        // Avanzamos la corrutina
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN (Entonces): Verificamos que pasó lo esperado
        assertEquals(null, viewModel.uiState.value.errorEmail)
        assertEquals(null, viewModel.uiState.value.errorContrasena)
        assertEquals(null, viewModel.uiState.value.generalError)

        // Verificamos que el ViewModel llamó al repositorio
        coVerify(exactly = 1) { repository.loginUser(email, pass) }
    }

    @Test
    fun `iniciarSesion con email vacio muestra error`() {
        // GIVEN
        viewModel.onEmailChange("")
        viewModel.onContrasenaChange("12345678")

        // WHEN
        viewModel.iniciarSesion()

        // THEN
        coVerify(exactly = 0) { repository.loginUser(any(), any()) }
        assertEquals("El correo no puede estar vacío", viewModel.uiState.value.errorEmail)
    }

    @Test
    fun `iniciarSesion fallido muestra error general`() = runTest {
        // GIVEN
        val email = "error@duoc.cl"
        val pass = "12345678"

        coEvery { repository.loginUser(email, pass) } returns Result.failure(Exception("Credenciales incorrectas"))

        viewModel.onEmailChange(email)
        viewModel.onContrasenaChange(pass)

        // WHEN
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.generalError)
    }
}