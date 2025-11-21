package com.example.feriafind_grupo13.data.model

import com.google.gson.annotations.SerializedName
// Esta clase mapea los datos del microservicio "Agenda"
data class Puesto(
    @SerializedName("idAgenda")
    val id: String,
    @SerializedName("idVendedor")
    val idVendedor: String,
    @SerializedName("idUbicacion") // En tu app lo llamabas idFeria
    val idFeria: String,
    @SerializedName("diaSemana")
    val diaSemana: String,
    @SerializedName("horaInicio")
    val horaInicio: String? = null,
    @SerializedName("horaTermino")
    val horaTermino: String? = null
)