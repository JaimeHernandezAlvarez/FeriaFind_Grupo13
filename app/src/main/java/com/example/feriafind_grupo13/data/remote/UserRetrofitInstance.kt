package com.example.feriafind_grupo13.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRetrofitInstance {
    private const val BASE_URL = "https://microuser.onrender.com/"

    // Variable para almacenar el Token en memoria
    private var jwtToken: String? = null

    fun setToken(token: String) {
        jwtToken = token
    }

    fun clearToken() {
        jwtToken = null
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()

            // Si tenemos token, lo agregamos como Bearer
            // Excluimos explícitamente el login si fuera necesario, pero por lo general
            // enviar el token al login no rompe nada, aunque es redundante.
            // Aquí lo agregamos siempre que exista.
            jwtToken?.let { token ->
                requestBuilder.header("Authorization", "Bearer $token")
            }

            chain.proceed(requestBuilder.build())
        }
        .build()

    val api: UserApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserApiService::class.java)
    }
}