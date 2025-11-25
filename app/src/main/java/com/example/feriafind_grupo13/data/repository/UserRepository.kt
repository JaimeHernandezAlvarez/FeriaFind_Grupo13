package com.example.feriafind_grupo13.data.repository

import android.util.Log
import com.example.feriafind_grupo13.data.local.storage.UserPreferences
import com.example.feriafind_grupo13.data.local.user.UserDao
import com.example.feriafind_grupo13.data.local.user.UserEntity
import com.example.feriafind_grupo13.data.remote.UserRetrofitInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import org.mindrot.jbcrypt.BCrypt

class UserRepository(
    private val userDao: UserDao,
    private val userPreferences: UserPreferences
) {

    // --- LOGIN ---
    suspend fun loginUser(email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Traemos TODOS los usuarios
                val response = UserRetrofitInstance.api.obtenerTodosLosUsuarios()

                if (response.isSuccessful && response.body() != null) {
                    val lista = response.body()?.embedded?.usuarios ?: emptyList()

                    // 2. Buscamos manualmente en la lista
                    val remoteUser = lista.find {
                        it.email.equals(email, ignoreCase = true)
                    }

                    if (remoteUser != null) {
                        // ¡ENCONTRADO!

                        // Generamos un hash nuevo válido para guardar en local
                        val passwordHash = BCrypt.hashpw(contrasena, BCrypt.gensalt())

                        val userEntity = UserEntity(
                            nombre = remoteUser.nombre,
                            email = remoteUser.email,
                            password = passwordHash, // Guardamos hash
                            descripcion = remoteUser.descripcion,
                            horario = remoteUser.horario,
                            fotoUri = remoteUser.fotoUrl
                        )

                        // Guardamos en local
                        val existingUser = userDao.findUserByEmail(email)
                        if (existingUser != null) {
                            userDao.updateUser(userEntity.copy(id = existingUser.id))
                        } else {
                            userDao.insertUser(userEntity)
                        }

                        // Sesión iniciada
                        userPreferences.setLoggedIn(true)
                        userPreferences.saveUserEmail(email)
                        Result.success(userEntity)
                    } else {
                        Result.failure(Exception("Usuario no encontrado en la nube."))
                    }
                } else {
                    // Falló la API, intentamos local
                    loginLocal(email, contrasena)
                }
            } catch (e: Exception) {
                Log.e("Login", "Error API: ${e.message}")
                loginLocal(email, contrasena)
            }
        }
    }

    // --- LOGIN LOCAL ---
    private suspend fun loginLocal(email: String, contrasenaPlana: String): Result<UserEntity> {
        val localUser = userDao.findUserByEmail(email)
        return if (localUser != null) {
            // Verificamos contraseña con BCrypt
            val isCorrect = try {
                BCrypt.checkpw(contrasenaPlana, localUser.password)
            } catch (e: Exception) {
                localUser.password == contrasenaPlana
            }

            if (isCorrect) {
                userPreferences.setLoggedIn(true)
                userPreferences.saveUserEmail(email)
                Result.success(localUser)
            } else {
                Result.failure(Exception("Contraseña incorrecta"))
            }
        } else {
            Result.failure(Exception("Usuario no encontrado localmente."))
        }
    }

    // --- REGISTRO ---
    suspend fun registerUser(nombre: String, email: String, contrasena: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Enviamos a la API
                val usuarioMap = mapOf(
                    "nombreUsuario" to nombre,
                    "correoElectronico" to email,
                    "password" to contrasena,
                    "descripcion" to "Nuevo usuario",
                    "foto" to ""
                )
                val response = UserRetrofitInstance.api.registrarUsuario(usuarioMap)

                if (response.isSuccessful) {
                    // Guardamos en local con Hash
                    val hashLocal = BCrypt.hashpw(contrasena, BCrypt.gensalt())
                    val newUserLocal = UserEntity(nombre = nombre, email = email, password = hashLocal)

                    userDao.insertUser(newUserLocal)
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error registro: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // MÉTODOS RESTANTES (Perfil, Update)
    suspend fun getUserProfile(email: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            val user = userDao.findUserByEmail(email)
            if (user != null) Result.success(user) else Result.failure(Exception("No user"))
        }
    }

    fun getLoggedInUserEmail(): Flow<String?> = userPreferences.getEmail

    suspend fun updateUserProfile(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Buscamos ID en la API primero
                val response = UserRetrofitInstance.api.obtenerTodosLosUsuarios()
                val lista = response.body()?.embedded?.usuarios ?: emptyList()

                val remoteUser = lista.find { it.email.equals(user.email, true) }
                val backendId = remoteUser?.id ?: throw Exception("Usuario no sincronizado")

                val mapaUpdate = mapOf(
                    "nombreUsuario" to user.nombre,
                    "descripcion" to (user.descripcion ?: ""),
                    "horario" to (user.horario ?: ""),
                    "foto" to (user.fotoUri ?: "")
                )

                val updateResponse = UserRetrofitInstance.api.actualizarUsuario(backendId, mapaUpdate)

                if (updateResponse.isSuccessful) {
                    userDao.updateUser(user)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error update: ${updateResponse.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}