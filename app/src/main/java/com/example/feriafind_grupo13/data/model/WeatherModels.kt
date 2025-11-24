package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName

// Respuesta completa de la API
data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather
)
// Datos específicos del clima actual
data class CurrentWeather(
    @SerializedName("temperature")
    val temperature: Double,
    @SerializedName("windspeed")
    val windspeed: Double,
    @SerializedName("weathercode")
    val weatherCode: Int // Código para saber si está nublado, sol, etc.
)