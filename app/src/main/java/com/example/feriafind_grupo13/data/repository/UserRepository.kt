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
    private val TAG = "FERIA_FIND_DEBUG"


    suspend fun loginUser(email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Intentando Login para: $email")
                val credenciales = mapOf("correoElectronico" to email, "password" to contrasena)
                val response = UserRetrofitInstance.api.loginUsuario(credenciales)

                if (response.isSuccessful && response.body() != null) {
                    val remoteUser = response.body()!!
                    Log.d(TAG, "Login exitoso en servidor. Usuario: ${remoteUser.nombre}")

                    val passwordHash = BCrypt.hashpw(contrasena, BCrypt.gensalt())
                    val userEntity = UserEntity(
                        nombre = remoteUser.nombre,
                        email = remoteUser.email,
                        password = passwordHash,
                        descripcion = remoteUser.descripcion,
                        horario = remoteUser.horario,
                        fotoUri = remoteUser.fotoUrl
                    )

                    val existingUser = userDao.findUserByEmail(email)
                    if (existingUser != null) {
                        userDao.updateUser(userEntity.copy(id = existingUser.id))
                    } else {
                        userDao.insertUser(userEntity)
                    }

                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(userEntity)
                } else {
                    if (response.code() == 401) Result.failure(Exception("Credenciales incorrectas"))
                    else Result.failure(Exception("Error Servidor: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error conexión: ${e.message}"))
            }
        }
    }

    suspend fun registerUser(nombre: String, email: String, contrasena: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Iniciando registro para: $email")
                // 1. Mapa exacto según tu modelo Java (Usuario.java)
                val usuarioMap = mapOf(
                    "nombreUsuario" to nombre,
                    "correoElectronico" to email,
                    "contrasena" to contrasena,
                    "descripcion" to "Nuevo usuario de FeriaFind", // Valor por defecto para evitar nulls
                    "foto" to "", // Valor vacío en lugar de null
                    "horario" to "9:00 - 18:00" // Valor por defecto
                )

                val response = UserRetrofitInstance.api.registrarUsuario(usuarioMap)

                if (response.isSuccessful) {
                    Log.d(TAG, "Registro exitoso en API")
                    // 2. Guardar sesión localmente para entrar directo
                    val hashLocal = BCrypt.hashpw(contrasena, BCrypt.gensalt())
                    val newUserLocal = UserEntity(
                        nombre = nombre,
                        email = email,
                        password = hashLocal,
                        descripcion = "Nuevo usuario de FeriaFind",
                        horario = "9:00 - 18:00"
                    )
                    userDao.insertUser(newUserLocal)
                    userPreferences.setLoggedIn(true)
                    userPreferences.saveUserEmail(email)
                    Result.success(Unit)
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "Fallo registro API: ${response.code()} - $errorBody")
                    Result.failure(Exception("Error registro: ${response.code()} $errorBody"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción Registro: ${e.message}")
                Result.failure(e)
            }
        }
    }

    // --- OBTENER PERFIL ---
    suspend fun getUserProfile(email: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            val user = userDao.findUserByEmail(email)
            if (user != null) Result.success(user) else Result.failure(Exception("Usuario no encontrado localmente"))
        }
    }

    fun getLoggedInUserEmail(): Flow<String?> = userPreferences.getEmail

    suspend fun updateUserProfile(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Buscar ID Remoto usando el correo
                val searchResponse = UserRetrofitInstance.api.buscarUsuarioPorEmail(user.email)

                if (!searchResponse.isSuccessful || searchResponse.body() == null) {
                    return@withContext Result.failure(Exception("No se pudo sincronizar con la nube."))
                }

                val backendId = searchResponse.body()!!.id // ID remoto (String)

                // 2. Preparar datos para PUT (usando nombres que espera el backend)
                val mapaUpdate = mapOf(
                    "nombreUsuario" to user.nombre,
                    "correoElectronico" to user.email,
                    "descripcion" to (user.descripcion ?: ""),
                    "horario" to (user.horario ?: ""),
                    "foto" to (user.fotoUri ?: "")
                    // OJO: No enviamos la contraseña si no se cambió para evitar problemas
                )

                // 3. Enviar a la API
                val updateResponse = UserRetrofitInstance.api.actualizarUsuario(backendId, mapaUpdate)

                if (updateResponse.isSuccessful) {
                    // 4. Si todo salió bien, actualizamos la base de datos local
                    userDao.updateUser(user)
                    Result.success(Unit)
                } else {
                    val error = updateResponse.errorBody()?.string()
                    Result.failure(Exception("Error servidor: ${updateResponse.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


    // --- ELIMINAR USUARIO (Delete) ---
    suspend fun deleteUser(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // 1. Buscar ID Remoto
                val searchResponse = UserRetrofitInstance.api.buscarUsuarioPorEmail(email)

                // Si el usuario no existe en la nube (error de consistencia),
                // permitimos borrarlo localmente para no bloquear la app.
                if (!searchResponse.isSuccessful || searchResponse.body() == null) {
                    userDao.deleteUserByEmail(email)
                    userPreferences.clearSession()
                    return@withContext Result.success(Unit)
                }

                val remoteId = searchResponse.body()!!.id

                // 2. Borrar en API
                val deleteResponse = UserRetrofitInstance.api.eliminarUsuario(remoteId)

                if (deleteResponse.isSuccessful) {
                    // 3. Borrar Localmente y cerrar sesión
                    userDao.deleteUserByEmail(email)
                    userPreferences.clearSession()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al borrar en servidor"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}