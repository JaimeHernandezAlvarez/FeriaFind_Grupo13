package com.example.feriafind_grupo13.data.remote

import com.example.feriafind_grupo13.data.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    @GET("v1/forecast")
    suspend fun getWeather(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current_weather") current: Boolean = true
    ): WeatherResponse
}