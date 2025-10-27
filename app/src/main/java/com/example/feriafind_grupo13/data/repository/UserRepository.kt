package com.example.feriafind_grupo13.data.repository

import com.example.feriafind_grupo13.data.local.storage.UserPreferences // <-- IMPORTAR
import com.example.feriafind_grupo13.data.local.user.UserDao
import com.example.feriafind_grupo13.data.local.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * El repositorio ahora también necesita UserPreferences
 * para guardar el estado de login.
 */
class UserRepository(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences // <-- AÑADIR
) {

    suspend fun registerUser(nombre: String, email: String, contrasena: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                if (userDao.findUserByEmail(email) != null) {
                    Result.failure(Exception("El correo electrónico ya está registrado."))
                } else {
                    val newUser = UserEntity(
                        nombre = nombre,
                        email = email,
                        password = contrasena
                    )
                    userDao.insertUser(newUser)

                    // --- CAMBIO AÑADIDO ---
                    // Al registrar exitosamente, guardamos el estado de login
                    userPreferences.setLoggedIn(true)
                    // --- FIN DE CAMBIO ---

                    Result.success(Unit)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun loginUser(email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.findUserByCredentials(email, contrasena)
                if (user != null) {

                    // --- CAMBIO AÑADIDO ---
                    // Al logear exitosamente, guardamos el estado de login
                    userPreferences.setLoggedIn(true)
                    // --- FIN DE CAMBIO ---

                    Result.success(user)
                } else {
                    Result.failure(Exception("Correo o contraseña incorrectos."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}