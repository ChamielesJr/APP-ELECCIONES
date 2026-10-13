package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa un Proceso Electoral Escolar (RF9).
 */
@Entity(
    tableName = "procesos_electorales",
    foreignKeys = [
        ForeignKey(
            entity = InstitucionEntity::class,
            parentColumns = ["id"],
            childColumns = ["institucionId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index(value = ["institucionId"])]
)
data class ProcesoElectoralEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val institucionId: Int,
    val titulo: String,
    val mensajeInstitucional: String = "",
    val fechaInicio: String,
    val horaInicio: String,
    val fechaFin: String,
    val horaFin: String,
    val estado: String = "PROGRAMADO", // PROGRAMADO, HABILITADO, SUSPENDIDO, FINALIZADO
    val fechaCreacion: String = ""
)
