package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados

import android.net.Uri

// Modelo inmutable para cada fila de lista/opción electoral
data class ResultadoListaState(
    val posicion: Int,
    val listaId: Int,
    val nombreLista: String,
    val nombrePresidente: String,
    val escudoUri: Uri? = null,
    val votos: Int = 0,
    val porcentaje: Float = 0f,
    val esVotoEspecial: Boolean = false
)

// Estado global de la pantalla de Resultados
data class ResultadosState(
    val totalVotos: Int = 0,
    val opcionesElectoralesCount: Int = 0,
    val resultadosListas: List<ResultadoListaState> = emptyList(),
    val isLoading: Boolean = false,
    val errorMensaje: String? = null
)