package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.data.model.ProductoResponse
import retrofit2.Response
import retrofit2.http.*
interface ProductApiService {

    // LEER
    @GET("api/v1/productos")
    suspend fun getProductos(): ProductoResponse

    // CREAR
    @POST("api/v1/productos")
    suspend fun createProducto(@Body producto: Producto): Response<Producto>

    // ACTUALIZAR (Asumiendo que el endpoint es /api/v1/productos/{id})
    @PUT("api/v1/productos/{id}")
    suspend fun updateProducto(@Path("id") id: Int, @Body producto: Producto): Response<Producto>

    // ELIMINAR
    @DELETE("api/v1/productos/{id}")
    suspend fun deleteProducto(@Path("id") id: Int): Response<Void>
}