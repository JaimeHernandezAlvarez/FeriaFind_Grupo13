package com.example.feriafind_grupo13.data.repository

import com.example.feriafind_grupo13.data.local.storage.UserPreferences // <-- IMPORTAR
import com.example.feriafind_grupo13.data.local.user.UserDao
import com.example.feriafind_grupo13.data.local.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
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
                    userPreferences.saveUserEmail(email)
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
                    userPreferences.saveUserEmail(user.email)
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
    // --- NUEVOS MÉTODOS PARA EL PERFIL ---

    /**
     * Obtiene el email del usuario logueado desde DataStore.
     */
    fun getLoggedInUserEmail(): Flow<String?> {
        return userPreferences.getEmail
    }

    /**
     * Obtiene los datos del perfil de un usuario por su email desde Room.
     */
    suspend fun getUserProfile(email: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val user = userDao.findUserByEmail(email)
                if (user != null) Result.success(user)
                else Result.failure(Exception("Usuario no encontrado"))
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Actualiza un usuario en la base de datos Room.
     */
    suspend fun updateUserProfile(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                userDao.updateUser(user)
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

}