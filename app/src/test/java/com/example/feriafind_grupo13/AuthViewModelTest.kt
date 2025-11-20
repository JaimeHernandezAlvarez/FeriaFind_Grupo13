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
    // Usamos Mockk para simular el comportamiento del repositorio sin tocar la base de datos real.
    private val repository: UserRepository = mockk()

    // 2. ViewModel a probar
    private lateinit var viewModel: AuthViewModel

    // 3. Dispatcher para pruebas de corrutinas
    // Necesario porque los ViewModels usan viewModelScope (Dispatchers.Main)
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        // Configuramos el dispatcher principal para que use nuestro testDispatcher
        Dispatchers.setMain(testDispatcher)
        // Inicializamos el ViewModel pasándole el repositorio falso (mock)
        viewModel = AuthViewModel(repository)
    }

    @After
    fun tearDown() {
        // Reseteamos el dispatcher al finalizar cada prueba
        Dispatchers.resetMain()
    }

    @Test
    fun `iniciarSesion exitoso actualiza estado y navega`() = runTest {
        // GIVEN (Dado): Un escenario donde el usuario y contraseña son correctos
        val email = "test@duoc.cl"
        val pass = "12345678"
        val fakeUser = UserEntity(1, "Test User", email, pass)

        // Le enseñamos al Mock qué responder cuando le pregunten
        coEvery { repository.loginUser(email, pass) } returns Result.success(fakeUser)

        // WHEN (Cuando): La UI ingresa los datos y presiona login
        viewModel.onEmailChange(email)
        viewModel.onContrasenaChange(pass)
        viewModel.iniciarSesion()

        // Importante: Avanzamos la corrutina para asegurar que se ejecute todo
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN (Entonces): Verificamos que pasó lo esperado

        // 1. No debe haber errores en los campos
        assertEquals(null, viewModel.uiState.value.errorEmail)
        assertEquals(null, viewModel.uiState.value.errorContrasena)

        // 2. No debe haber error general
        assertEquals(null, viewModel.uiState.value.generalError)

        // 3. Verificamos que el ViewModel realmente llamó al repositorio
        coVerify(exactly = 1) { repository.loginUser(email, pass) }
    }

    @Test
    fun `iniciarSesion con email vacio muestra error`() {
        // GIVEN: El usuario deja el email vacío
        viewModel.onEmailChange("")
        viewModel.onContrasenaChange("12345678")

        // WHEN: Intenta iniciar sesión
        viewModel.iniciarSesion()

        // THEN:
        // 1. El repositorio NO debe ser llamado (ahorramos recursos)
        coVerify(exactly = 0) { repository.loginUser(any(), any()) }

        // 2. El estado debe tener un mensaje de error específico
        assertEquals("El correo no puede estar vacío", viewModel.uiState.value.errorEmail)
    }

    @Test
    fun `iniciarSesion fallido muestra error general`() = runTest {
        // GIVEN: Credenciales incorrectas (simulado)
        val email = "error@duoc.cl"
        val pass = "12345678"

        // El repositorio responde con fallo
        coEvery { repository.loginUser(email, pass) } returns Result.failure(Exception("Credenciales incorrectas"))

        viewModel.onEmailChange(email)
        viewModel.onContrasenaChange(pass)

        // WHEN
        viewModel.iniciarSesion()
        testDispatcher.scheduler.advanceUntilIdle()

        // THEN
        // El ViewModel debe exponer el mensaje de error para que la UI lo muestre (ej. un Snackbar)
        assertEquals("Credenciales incorrectas", viewModel.uiState.value.generalError)
    }
}