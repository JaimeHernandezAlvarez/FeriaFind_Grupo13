package com.example.feriafind_grupo13.data.repository

import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.data.remote.ProductRetrofitInstance

class ProductRepository {

    suspend fun getProductos(): List<Producto> {
        val response = ProductRetrofitInstance.api.getProductos()
        return response.embedded?.productos ?: emptyList()
    }
}