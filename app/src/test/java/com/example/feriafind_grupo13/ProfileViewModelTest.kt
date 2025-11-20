package com.example.feriafind_grupo13

import com.example.feriafind_grupo13.data.local.user.UserEntity
import com.example.feriafind_grupo13.data.repository.UserRepository
import com.example.feriafind_grupo13.viewmodel.ProfileViewModel
import io.mockk.coEvery
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
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val repository: UserRepository = mockk()
    private lateinit var viewModel: ProfileViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `carga inicial obtiene datos del usuario logueado`() = runTest {
        // GIVEN
        val email = "test@duoc.cl"
        val userEntity = UserEntity(1, "Juan", email, "pass", "Vendedor de prueba", "9-18", null)

        // Simulamos que hay un usuario logueado
        every { repository.getLoggedInUserEmail() } returns flowOf(email)
        // Simulamos la respuesta de la BD
        coEvery { repository.getUserProfile(email) } returns Result.success(userEntity)

        // WHEN: Inicializamos el VM (esto dispara loadUserProfile)
        viewModel = ProfileViewModel(repository)

        // THEN
        val state = viewModel.uiState.value
        assertEquals("Juan", state.nombre)
        assertEquals("Vendedor de prueba", state.descripcion)
        assertEquals("9-18", state.horario)
        assertEquals(email, state.correo)
    }

    @Test
    fun `guardarCambios actualiza el perfil en repositorio`() = runTest {
        // GIVEN
        val email = "test@duoc.cl"
        val originalUser = UserEntity(1, "Juan", email, "pass")

        every { repository.getLoggedInUserEmail() } returns flowOf(email)
        coEvery { repository.getUserProfile(email) } returns Result.success(originalUser)
        // Simulamos éxito al actualizar
        coEvery { repository.updateUserProfile(any()) } returns Result.success(Unit)

        viewModel = ProfileViewModel(repository)

        // WHEN: Cambiamos datos y guardamos
        viewModel.onNombreChange("Juan Actualizado")
        viewModel.guardarCambios()

        // THEN
        // Verificamos que se llamó al repositorio con el nuevo nombre
        coVerify {
            repository.updateUserProfile(match { it.nombre == "Juan Actualizado" })
        }
    }
}