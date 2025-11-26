package com.example.feriafind_grupo13.data.remote

import android.util.Base64
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UserRetrofitInstance {
    // Tu URL de Render para usuarios
    private const val BASE_URL = "https://microuser.onrender.com/"

    // Variable para guardar la "llave" de acceso (Basic Auth Header)
    private var authHeader: String? = null
    // Función para configurar las credenciales cuando el usuario se loguea
    fun setCredentials(user: String, pass: String) {
        val credentials = "$user:$pass"
        val encodedCredentials = Base64.encodeToString(credentials.toByteArray(), Base64.NO_WRAP)
        authHeader = "Basic $encodedCredentials"
    }
    // Función para borrar credenciales al cerrar sesión
    fun clearCredentials() {
        authHeader = null
    }
    // Cliente HTTP personalizado que inyecta la cabecera de autorización
    private val client = OkHttpClient.Builder()
        // 1. AÑADIMOS EL LOGGING INTERCEPTOR PARA VER EL JSON EN EL LOGCAT
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        // 2. INTERCEPTOR DE SEGURIDAD (Auth)
        .addInterceptor { chain ->
            val originalRequest = chain.request()
            // No enviamos Auth en Login ni Registro para evitar conflictos
            if (originalRequest.url.encodedPath.contains("/login") ||
                (originalRequest.url.encodedPath.contains("/usuarios") && originalRequest.method == "POST")) {
                return@addInterceptor chain.proceed(originalRequest)
            }

            val requestBuilder = originalRequest.newBuilder()
            authHeader?.let {
                requestBuilder.header("Authorization", it)
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