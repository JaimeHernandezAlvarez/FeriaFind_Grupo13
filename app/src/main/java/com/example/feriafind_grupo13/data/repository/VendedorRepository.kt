package com.example.feriafind_grupo13.data.repository

import com.example.feriafind_grupo13.data.model.Vendedor
import com.example.feriafind_grupo13.data.remote.RetrofitInstance

class VendedorRepository {

    // Función que será llamada por el ViewModel para obtener los datos
    suspend fun getVendedores(): List<Vendedor> {
        val response = RetrofitInstance.api.getVendedores()
        // 2. Nosotros extraemos solo la lista 'vendedorList' que está dentro
        // El operador ?. evita que la app se cierre si viene vacío
        return response.embedded?.vendedores ?: emptyList()
    }

    // Función opcional para obtener un solo vendedor
    suspend fun getVendedor(id: Int): Vendedor {
        return RetrofitInstance.api.getVendedorById(id)
    }
}