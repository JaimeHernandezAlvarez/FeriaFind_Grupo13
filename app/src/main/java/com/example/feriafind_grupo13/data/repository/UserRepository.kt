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
                // Preparamos los datos para enviar
                val credenciales = mapOf(
                    "correoElectronico" to email, // Asegúrate que coincida con lo que espera el Backend
                    "password" to contrasena
                )

                // --- LLAMADA AL NUEVO ENDPOINT ---
                val response = UserRetrofitInstance.api.loginUsuario(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    val remoteUser = response.body()!!

                    // ¡ÉXITO! El servidor dijo que sí.
                    // Guardamos en local...
                    val passwordHash = BCrypt.hashpw(contrasena, BCrypt.gensalt())

                    val userEntity = UserEntity(
                        nombre = remoteUser.nombre,
                        email = remoteUser.email,
                        password = passwordHash,
                        descripcion = remoteUser.descripcion,
                        horario = remoteUser.horario,
                        fotoUri = remoteUser.fotoUrl
                    )

                    // Guardado en BD local (Room)
                    val existingUser = userDao.findUserByEmail(email)
                    if (existingUser != null) {
                        userDao.updateUser(userEntity.copy(id = existingUser.id))
                    } else {
                        userDao.insertUser(userEntity)
                    }

                    // Preferencias
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)

                    Result.success(userEntity)

                } else {
                    // Si el servidor devuelve 401, entra aquí
                    if (response.code() == 401) {
                        Result.failure(Exception("Correo o contraseña incorrectos"))
                    } else {
                        Result.failure(Exception("Error Servidor: ${response.code()}"))
                    }
                }
            } catch (e: Exception) {
                Log.e("LOGIN_DEBUG", "Error conexión: ${e.message}")
                Result.failure(Exception("Error de conexión con el servidor"))
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
                // 1. Buscamos al usuario en la nube usando su email para OBTENER SU ID
                // Ya no usamos obtenerTodosLosUsuarios()
                val searchResponse = UserRetrofitInstance.api.buscarUsuarioPorEmail(user.email)

                // Verificamos si lo encontramos
                if (!searchResponse.isSuccessful || searchResponse.body() == null) {
                    throw Exception("No se pudo sincronizar: Usuario no encontrado en la nube.")
                }

                val remoteUser = searchResponse.body()!!
                // Aquí obtenemos el ID que necesitamos para el PUT
                val backendId = remoteUser.id

                if (backendId.isEmpty()) {
                    throw Exception("Error: El usuario remoto no tiene ID válido.")
                }

                // 2. Preparamos los datos para actualizar
                val mapaUpdate = mapOf(
                    "nombreUsuario" to user.nombre,
                    "correoElectronico" to user.email, // Es bueno enviarlo también por si acaso
                    "descripcion" to (user.descripcion ?: ""),
                    "horario" to (user.horario ?: ""),
                    "foto" to (user.fotoUri ?: "")
                    // NOTA: No enviamos la contraseña aquí para no sobrescribirla con nulos
                )

                // 3. Ejecutamos la actualización (PUT)
                val updateResponse = UserRetrofitInstance.api.actualizarUsuario(backendId, mapaUpdate)

                if (updateResponse.isSuccessful) {
                    // Actualizamos también la base de datos local (Room)
                    userDao.updateUser(user)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al actualizar en servidor: ${updateResponse.code()}"))
                }

            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}