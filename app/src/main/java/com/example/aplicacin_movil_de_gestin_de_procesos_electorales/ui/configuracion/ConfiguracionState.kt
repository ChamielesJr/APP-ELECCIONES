package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import android.net.Uri

data class ConfiguracionState(
    val esModoEdicion: Boolean = true,
    val estado: String = "En curso",
    val votacionAbierta: Boolean = true,
    val nombreInstitucion: String = "",
    val mensajeInstitucional: String = "",
    val nombreEleccion: String = "",
    val escudoUri: Uri? = null,
    val fechaInicio: String = "",
    val horaInicio: String = "",
    val fechaFin: String = "",
    val horaFin: String = "",
    val habilitarEleccion: Boolean = true,
    val publicarResultados: Boolean = true,

    val errorNombreInstitucion: Boolean = false,
    val errorMensajeInstitucional: Boolean = false,
    val errorNombreEleccion: Boolean = false,
    val errorEscudo: Boolean = false
) {
    companion object {
        const val MAX_MENSAJE = 500
    }
}