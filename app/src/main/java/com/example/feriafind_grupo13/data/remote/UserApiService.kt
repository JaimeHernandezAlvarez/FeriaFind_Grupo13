package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Usuario
import com.example.feriafind_grupo13.data.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApiService {
    // REGISTRO
    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body usuario: Map<String, String>): Response<Usuario>

    // --- NUEVO LOGIN (Usamos POST para saltar la seguridad del GET) ---
    @POST("api/v1/usuarios/login")
    suspend fun loginUsuario(@Body credenciales: Map<String, String>): Response<Usuario>

    // NUEVO MÉTODO: Buscar un solo usuario por correo
    @GET("api/v1/usuarios/buscar")
    suspend fun buscarUsuarioPorEmail(@Query("email") email: String): Response<Usuario>

    // ACTUALIZAR (PUT)
    @PUT("api/v1/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: String,
        @Body usuario: Map<String, String>
    ): Response<Usuario>
}