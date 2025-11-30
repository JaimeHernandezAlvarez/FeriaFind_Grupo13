package com.example.feriafind_grupo13.data.repository

import com.example.feriafind_grupo13.data.model.Producto
import com.example.feriafind_grupo13.data.remote.ProductRetrofitInstance

class ProductRepository {

    suspend fun getProductos(): List<Producto> {
        val response = ProductRetrofitInstance.api.getProductos()
        return response.embedded?.productos ?: emptyList()
    }
        suspend fun createProducto(producto: Producto): Result<Producto> {
            return try {
                val response = ProductRetrofitInstance.api.createProducto(producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al crear: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        suspend fun updateProducto(producto: Producto): Result<Producto> {
            return try {
                val response = ProductRetrofitInstance.api.updateProducto(producto.id, producto)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Error al actualizar: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
        suspend fun deleteProducto(id: Int): Result<Unit> {
            return try {
                val response = ProductRetrofitInstance.api.deleteProducto(id)
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al eliminar: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }