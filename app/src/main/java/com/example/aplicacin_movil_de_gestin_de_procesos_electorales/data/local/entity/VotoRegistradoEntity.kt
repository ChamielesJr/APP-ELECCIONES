package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para registrar los votos emitidos de forma totalmente ANÓNIMA.
 * No contiene ninguna columna referenciando al estudiante para proteger el secreto del voto.
 */
@Entity(
    tableName = "votos_registrados",
    foreignKeys = [
        ForeignKey(
            entity = ProcesoElectoralEntity::class,
            parentColumns = ["id"],
            childColumns = ["procesoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["procesoId"])]
)
data class VotoRegistradoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val procesoId: Int,
    val listaElectoralId: Int? = null, // Null indica Voto Nulo
    val esVotoNulo: Boolean = false,
    val fechaHora: String = ""
)
