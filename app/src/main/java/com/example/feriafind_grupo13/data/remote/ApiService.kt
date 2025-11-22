package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Vendedor
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {


    // Solicitamos la respuesta "envuelta"
    @GET("api/v1/vendedores")
    suspend fun getVendedores(): VendedorResponse

    // Para obtener un solo vendedor por ID
    @GET("api/v1/vendedores/{id}")
    suspend fun getVendedorById(@Path("id") id: Int): Vendedor
}