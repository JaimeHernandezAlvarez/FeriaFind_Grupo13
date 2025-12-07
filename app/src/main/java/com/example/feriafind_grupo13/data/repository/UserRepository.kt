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
    private val TAG = "FERIA_FIND_REPO"

    // --- LOGIN ---
    // (Este se mantiene igual que la versión anterior corregida)
    suspend fun loginUser(email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                UserRetrofitInstance.clearToken()
                // 1. Login
                val credenciales = mapOf("correoElectronico" to email, "password" to contrasena)
                val responseLogin = UserRetrofitInstance.api.loginUsuario(credenciales)

                if (responseLogin.isSuccessful && responseLogin.body() != null) {
                    val token = responseLogin.body()!!.token
                    guardarSesionYDescargarPerfil(token, email, contrasena)
                } else {
                    val errorBody = responseLogin.errorBody()?.string()
                    Result.failure(Exception("Error Login (${responseLogin.code()}): $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(Exception("Error conexión: ${e.message}"))
            }
        }
    }

    // --- REGISTRO (Optimizado con Token directo) ---
    suspend fun registerUser(nombre: String, email: String, contrasena: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                UserRetrofitInstance.clearToken()

                val usuarioMap = mapOf(
                    "nombreUsuario" to nombre,
                    "correoElectronico" to email,
                    "contrasena" to contrasena,
                    "rol" to "USER",
                    "descripcion" to "Nuevo usuario",
                    "foto" to "https://i.pravatar.cc/150",
                    "horario" to "Por definir"
                )

                // 1. LLAMADA DE REGISTRO
                val responseRegister = UserRetrofitInstance.api.registrarUsuario(usuarioMap)

                if (responseRegister.isSuccessful && responseRegister.body() != null) {
                    // 2. OBTENEMOS EL TOKEN DIRECTAMENTE DEL REGISTRO
                    val token = responseRegister.body()!!.token
                    Log.d(TAG, "Registro exitoso. Token recibido: $token")

                    // 3. REUTILIZAMOS LA LÓGICA PARA GUARDAR Y DESCARGAR PERFIL
                    guardarSesionYDescargarPerfil(token, email, contrasena)

                } else {
                    val errorBody = responseRegister.errorBody()?.string()
                    Result.failure(Exception("Error registro: ${responseRegister.code()} $errorBody"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- FUNCIÓN AUXILIAR (Para no repetir código entre Login y Registro) ---
    private suspend fun guardarSesionYDescargarPerfil(token: String, email: String, pass: String): Result<UserEntity> {
        // A. Configurar Token en memoria y disco
        UserRetrofitInstance.setToken(token)
        userPreferences.saveAuthToken(token)

        // B. Descargar Perfil (Ya autenticado con el token)
        val responseUser = UserRetrofitInstance.api.buscarUsuarioPorEmail(email)

        if (responseUser.isSuccessful && responseUser.body() != null) {
            val remoteUser = responseUser.body()!!

            // C. Guardar en Base de Datos Local
            val existingUser = userDao.findUserByEmail(email)
            val localId = existingUser?.id ?: 0L

            val userEntity = UserEntity(
                id = localId,
                remoteId = remoteUser.id,
                nombre = remoteUser.nombre,
                email = remoteUser.email,
                password = pass,
                descripcion = remoteUser.descripcion ?: "",
                horario = remoteUser.horario ?: "",
                fotoUri = remoteUser.fotoUrl ?: ""
            )

            userDao.insertUser(userEntity)
            userPreferences.setLoggedIn(true)
            userPreferences.saveUserEmail(email)

            return Result.success(userEntity)
        } else {
            return Result.failure(Exception("Token recibido, pero falló descarga de perfil."))
        }
    }

    // --- OBTENER PERFIL (Local) ---
    suspend fun getUserProfile(email: String): Result<UserEntity> {
        return withContext(Dispatchers.IO) {
            val user = userDao.findUserByEmail(email)
            if (user != null) {
                // NOTA: Aquí deberías recuperar el token de UserPreferences si la app se reinició
                // val token = userPreferences.getToken()
                // UserRetrofitInstance.setToken(token)
                Result.success(user)
            } else {
                Result.failure(Exception("Usuario no encontrado localmente"))
            }
        }
    }

    // --- ACTUALIZAR PERFIL ---
    suspend fun updateUserProfile(user: UserEntity): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val backendId = user.remoteId
                val mapaUpdate = mapOf(
                    "nombreUsuario" to user.nombre,
                    "correoElectronico" to user.email,
                    "descripcion" to (user.descripcion ?: ""),
                    "horario" to (user.horario ?: ""),
                    "foto" to (user.fotoUri ?: "")
                    // "contrasena" no la enviamos si no cambió
                )

                val response = UserRetrofitInstance.api.actualizarUsuario(backendId, mapaUpdate)

                if (response.isSuccessful) {
                    userDao.updateUser(user)
                    Result.success(Unit)
                } else {
                    if(response.code() == 403 || response.code() == 401) {
                        Result.failure(Exception("Sesión expirada"))
                    } else {
                        val error = response.errorBody()?.string()
                        Result.failure(Exception("Error actualizar: $error"))
                    }
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    // --- ELIMINAR ---
    // (Similar a lo anterior, usando el token ya configurado)
    suspend fun deleteUser(email: String): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val userLocal = userDao.findUserByEmail(email)
                val remoteId = userLocal?.remoteId

                if (!remoteId.isNullOrBlank()) {
                    val response = UserRetrofitInstance.api.eliminarUsuario(remoteId)
                    if (!response.isSuccessful) {
                        return@withContext Result.failure(Exception("Error eliminando en servidor"))
                    }
                }
                userDao.deleteUserByEmail(email)
                userPreferences.clearSession()
                UserRetrofitInstance.clearToken()
                Result.success(Unit)
            } catch(e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getLoggedInUserEmail(): Flow<String?> = userPreferences.getEmail
    val getAuthToken: Flow<String?> = userPreferences.getAuthToken
}