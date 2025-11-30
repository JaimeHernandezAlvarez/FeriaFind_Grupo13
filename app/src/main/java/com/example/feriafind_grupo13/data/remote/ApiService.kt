package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Vendedor
import com.example.feriafind_grupo13.data.model.VendedorResponse
import retrofit2.Response
import retrofit2.http.*
interface ApiService {


    //LEER
    @GET("api/v1/vendedores")
    suspend fun getVendedores(): VendedorResponse

    //LEER POR ID
    @GET("api/v1/vendedores/{id}")
    suspend fun getVendedorById(@Path("id") id: Int): Vendedor

    //CREAR
    @POST("api/v1/vendedores")
    suspend fun createVendedor(@Body vendedor: Vendedor): Response<Vendedor>

    //ACTUALIZAR
    @PUT("api/v1/vendedores/{id}")
    suspend fun updateVendedor(@Path("id") id: String, @Body vendedor: Vendedor): Response<Vendedor>

    //BORRAR
    @DELETE("api/v1/vendedores/{id}")
    suspend fun deleteVendedor(@Path("id") id: String): Response<Void>
}