package com.example.feriafind_grupo13.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ProductRetrofitInstance {

    // CAMBIO IMPORTANTE: Esta URL apunta al microservicio de PRODUCTOS
    private const val BASE_URL = "https://microprod.onrender.com/"

    val api: ProductApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ProductApiService::class.java)
    }
}