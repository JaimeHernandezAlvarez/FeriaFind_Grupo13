package com.example.feriafind_grupo13.data.repository

import android.util.Log
import com.example.feriafind_grupo13.data.local.storage.UserPreferences
import com.example.feriafind_grupo13.data.local.user.UserDao
import com.example.feriafind_grupo13.data.local.user.UserEntity
import com.example.feriafind_grupo13.data.remote.UserRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class UserRepository(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {

    // --- REGISTRO EN NUBE ---
    suspend fun registerUser(nombre: String, email: String, contrasena: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Crear objeto JSON simple para enviar al microservicio
                val usuarioMap = mapOf(
                    "nombreUsuario" to nombre,
                    "correoElectronico" to email,
                    "password" to contrasena, // Asumiendo que tu API recibe 'password'
                    "descripcion" to "Usuario nuevo",
                    "foto" to ""
                )

                val response = UserRetrofitInstance.api.registrarUsuario(usuarioMap)

                if (response.isSuccessful) {
                    // Guardamos en local también para mantener sesión offline si se requiere
                    val newUserLocal = UserEntity(nombre = nombre, email = email, password = contrasena)
                    userDao.insertUser(newUserLocal)

                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error API: ${response.code()}"))
                }
            } catch (e: Exception) {
                Log.e("UserRepository", "Error registro", e)
                Result.failure(e)
            }
        }
    }

    // --- LOGIN (Híbrido: Intenta Nube, si falla busca Local) ---
    suspend fun loginUser(email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Intentar verificar con API (Simulación buscando por correo)
                // Nota: Esto es un "truco" para MVP. Lo ideal es un endpoint POST /login que devuelva Token.
                // Aquí asumimos que si el usuario existe y la password local coincide (o simplemente confiamos en el registro).

                // Para este ejemplo MVP, verificaremos credenciales en base de datos LOCAL
                // (suponiendo que se registró en este cel) O podrías implementar la lógica de validar contra la API.

                val localUser = userDao.findUserByCredentials(email, contrasena)

                if (localUser != null) {
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(localUser)
                } else {
                    Result.failure(Exception("Credenciales inválidas o usuario no registrado en este dispositivo."))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
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