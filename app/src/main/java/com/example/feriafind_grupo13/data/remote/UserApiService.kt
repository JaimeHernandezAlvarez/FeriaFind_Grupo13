package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.LoginResponse
import com.example.feriafind_grupo13.data.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    // REGISTRO
    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body usuario: Any): Response<LoginResponse>
    // Nota: Si registro tambien devuelve token, usa LoginResponse, si no devuelve nada usa Void o Unit

    // LOGIN: Devuelve solo el Token
    @POST("api/v1/usuarios/login")
    suspend fun loginUsuario(@Body credenciales: Any): Response<LoginResponse>

    // BUSCAR: Requiere Token (Bearer)
    @GET("api/v1/usuarios/buscar")
    suspend fun buscarUsuarioPorEmail(@Query("email") email: String): Response<Usuario>

    // ACTUALIZAR
    @PUT("api/v1/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: String,
        @Body usuario: Any
    ): Response<Usuario>

    // ELIMINAR
    @DELETE("api/v1/usuarios/{id}")
    suspend fun eliminarUsuario(@Path("id") id: String): Response<Void>
}