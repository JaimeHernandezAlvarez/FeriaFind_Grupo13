package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Usuario
import com.example.feriafind_grupo13.data.model.UsuarioResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApiService {
    // REGISTRO
    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body usuario: Map<String, String>): Response<Usuario>

    // [CORREGIDO] "Traer todos" en vez de "Buscar por correo" (que no existía)
    @GET("api/v1/usuarios")
    suspend fun obtenerTodosLosUsuarios(): Response<UsuarioResponse>

    // ACTUALIZAR (PUT)
    @PUT("api/v1/usuarios/{id}")
    suspend fun actualizarUsuario(
        @Path("id") id: String,
        @Body usuario: Map<String, String>
    ): Response<Usuario>
}