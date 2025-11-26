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
    // --- LOGIN ---
    private val TAG = "FERIA_FIND_REPO"
    suspend fun loginUser(email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                UserRetrofitInstance.clearCredentials()

                Log.d(TAG, "Login para: $email")

                val credenciales = mapOf("correoElectronico" to email, "password" to contrasena)

                val response = UserRetrofitInstance.api.loginUsuario(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    val remoteUser = response.body()!!

                    UserRetrofitInstance.setCredentials(email, contrasena)

                    // Mantener ID local si existe
                    val existingUser = userDao.findUserByEmail(email)
                    val localId = existingUser?.id ?: 0L
                    val passwordToSave = contrasena

                    // Mapear respuesta del servidor a entidad local
                    val userEntity = UserEntity(
                        id = localId,
                        remoteId = remoteUser.id,
                        nombre = remoteUser.nombre,
                        email = remoteUser.email,
                        password = passwordToSave,
                        descripcion = remoteUser.descripcion ?: "",
                        horario = remoteUser.horario ?: "",
                        fotoUri = remoteUser.fotoUrl ?: ""
                    )

                    userDao.insertUser(userEntity)
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(userEntity)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Login fallido (Code ${response.code()}): $errorBody")
                    Result.failure(Exception("Error Login (${response.code()}): $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error conexión: ${e.message}"))
            }
        }
    }

    // --- REGISTRO ---
    suspend fun registerUser(nombre: String, email: String, contrasena: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                UserRetrofitInstance.clearCredentials() // Limpieza preventiva

                val usuarioMap = mapOf(
                    "nombreUsuario" to nombre,
                    "correoElectronico" to email,
                    "contrasena" to contrasena,
                    "descripcion" to "¡Nuevo usuario!",
                    "foto" to "",
                    "horario" to "Por definir"
                )

                val response = UserRetrofitInstance.api.registrarUsuario(usuarioMap)

                if (response.isSuccessful && response.body() != null) {
                    val createdUser = response.body()!!

                    UserRetrofitInstance.setCredentials(email, contrasena)

                    val newUserLocal = UserEntity(
                        remoteId = createdUser.id,
                        nombre = nombre,
                        email = email,
                        password = contrasena,
                        descripcion = "¡Nuevo usuario!",
                        horario = "Por definir",
                        fotoUri = ""
                    )
                    userDao.insertUser(newUserLocal)
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Error registro: ${response.code()} $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- OBTENER PERFIL ---
    suspend fun getUserProfile(email: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            val user = userDao.findUserByEmail(email)
            if (user != null) {
                UserRetrofitInstance.setCredentials(user.email, user.password)
                Result.success(user) }
            else {
                Result.failure(Exception("Usuario no encontrado"))
            }
        }
    }
    fun getLoggedInUserEmail(): Flow<String?> = userPreferences.getEmail

    // --- ACTUALIZAR PERFIL ---
    suspend fun updateUserProfile(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {

                UserRetrofitInstance.setCredentials(user.email, user.password)

                val backendId = user.remoteId
                if (backendId.isBlank()) {
                    return@withContext Result.failure(Exception("Error: Reinicia sesión para sincronizar tu cuenta."))
                }
                val mapaUpdate = mutableMapOf<String, String>()
                mapaUpdate["nombreUsuario"] = user.nombre
                mapaUpdate["correoElectronico"] = user.email
                mapaUpdate["descripcion"] = user.descripcion ?: ""
                mapaUpdate["horario"] = user.horario ?: ""
                mapaUpdate["foto"] = user.fotoUri ?: ""
                mapaUpdate["contrasena"] = user.password

                Log.d(TAG, "Enviando Update a ID: $backendId con ${mapaUpdate.keys}")

                val response = UserRetrofitInstance.api.actualizarUsuario(backendId, mapaUpdate)

                if (response.isSuccessful) {
                    Log.d(TAG, "¡Update Exitoso!")
                    // Solo si el servidor dice OK, guardamos en el celular
                    userDao.updateUser(user)
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Result.failure(Exception("Error servidor (${response.code()}): $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    // --- ELIMINAR ---
    suspend fun deleteUser(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Buscamos usuario local para obtener su remoteId
                val userLocal = userDao.findUserByEmail(email)
                if (userLocal != null) {
                    UserRetrofitInstance.setCredentials(userLocal.email, userLocal.password)
                }
                val remoteId = userLocal?.remoteId
                if (!remoteId.isNullOrBlank()) {
                    val response = UserRetrofitInstance.api.eliminarUsuario(remoteId)
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("Error al eliminar en servidor"))
                    }
                }
                // Si no tiene remoteId o ya se borró, limpiamos local
                userDao.deleteUserByEmail(email)
                userPreferences.clearSession()
                UserRetrofitInstance.clearCredentials()
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}