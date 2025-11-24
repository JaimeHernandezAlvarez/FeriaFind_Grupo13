package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UserApiService {

    // REGISTRO: Según tu Swagger POST /api/v1/usuarios
    @POST("api/v1/usuarios")
    suspend fun registrarUsuario(@Body usuario: Map<String, String>): Response<Usuario>

    // LOGIN: Como no vi un endpoint explícito "/login" en tu foto,
    // usaremos una búsqueda por email para validar (común en MVP académicos si no hay Auth Controller)
    // Si tienes un endpoint de login real, cámbialo aquí.
    @GET("api/v1/usuarios/search/findByCorreoElectronico")
    suspend fun buscarPorCorreo(@retrofit2.http.Query("correo") correo: String): Response<Usuario>

    // OPCIONAL: Obtener usuario por ID
    @GET("api/v1/usuarios/{id}")
    suspend fun getUsuarioById(@Path("id") id: String): Response<Usuario>
}