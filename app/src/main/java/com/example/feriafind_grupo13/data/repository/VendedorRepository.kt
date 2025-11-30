package com.example.feriafind_grupo13.data.repository

import com.example.feriafind_grupo13.data.model.Vendedor
import com.example.feriafind_grupo13.data.remote.RetrofitInstance

class VendedorRepository {

    suspend fun getVendedores(): List<Vendedor> {
        val response = RetrofitInstance.api.getVendedores()
        return response.embedded?.vendedores ?: emptyList()
    }
    suspend fun getVendedor(id: Int): Vendedor {
        return RetrofitInstance.api.getVendedorById(id)
    }
    suspend fun createVendedor(vendedor: Vendedor): Result<Vendedor> {
        return try {
            val response = RetrofitInstance.api.createVendedor(vendedor)
            if (response.isSuccessful && response.body() != null)
                Result.success(response.body()!!)
            else Result.failure(Exception("Error creando vendedor"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateVendedor(vendedor: Vendedor): Result<Vendedor> {
        return try {
            val response = RetrofitInstance.api.updateVendedor(vendedor.id, vendedor)
            if (response.isSuccessful && response.body() != null) Result.success(response.body()!!)
            else Result.failure(Exception("Error actualizando vendedor"))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteVendedor(id: String): Result<Unit> {
        return try {
            val response = RetrofitInstance.api.deleteVendedor(id)
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Error eliminando vendedor"))
        } catch (e: Exception) { Result.failure(e) }
    }
}