package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.remote.ProductoResponse
import retrofit2.http.GET

interface ProductApiService {

    // Endpoint específico del microservicio de productos
    @GET("api/v1/productos")
    suspend fun getProductos(): ProductoResponse
}