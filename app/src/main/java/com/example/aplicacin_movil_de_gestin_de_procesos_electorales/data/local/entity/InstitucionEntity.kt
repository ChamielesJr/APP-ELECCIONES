package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa una Institución Educativa perteneciente al Distrito 13D02.
 */
@Entity(tableName = "instituciones")
data class InstitucionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val codigoInstitucional: String,
    val direccion: String,
    val telefono: String,
    val correo: String,
    val distrito: String = "13D02",
    val estado: Boolean = true,
    val fechaRegistro: String,
)
