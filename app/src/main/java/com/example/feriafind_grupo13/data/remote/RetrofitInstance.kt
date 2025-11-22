package com.example.feriafind_grupo13.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    // URL base de tu backend desplegado en Render
    // Nota: Retrofit siempre requiere que termine en '/'
    private const val BASE_URL = "https://microvend.onrender.com/"

    val api: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}