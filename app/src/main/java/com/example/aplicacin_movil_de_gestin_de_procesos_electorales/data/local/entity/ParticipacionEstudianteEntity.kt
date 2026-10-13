package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para registrar la participación de un estudiante en un proceso electoral.
 * La restricción de unicidad UNIQUE(estudianteId, procesoId) impide votos duplicados.
 */
@Entity(
    tableName = "participacion_estudiantes",
    foreignKeys = [
        ForeignKey(
            entity = EstudianteEntity::class,
            parentColumns = ["id"],
            childColumns = ["estudianteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProcesoElectoralEntity::class,
            parentColumns = ["id"],
            childColumns = ["procesoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["estudianteId", "procesoId"], unique = true),
        Index(value = ["procesoId"])
    ]
)
data class ParticipacionEstudianteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val estudianteId: Int,
    val procesoId: Int,
    val fechaHora: String = ""
)
