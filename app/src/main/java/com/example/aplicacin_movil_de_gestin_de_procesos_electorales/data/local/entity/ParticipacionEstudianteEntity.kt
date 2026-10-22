package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para registrar el control de participación de un estudiante en un proceso electoral específico.
 * Pertenece a la capa de datos (Data Layer) y mapea la tabla SQLite 'participacion_estudiantes'.
 *
 * RATIONALE DE SEGURIDAD Y DISEÑO:
 * Esta tabla garantiza que un estudiante vote UNA SOLA VEZ por proceso electoral mediante el índice único
 * UNIQUE(estudianteId, procesoId). Se mantiene independiente de los sufragios emitidos para resguardar
 * el secreto del voto.
 *
 * @property id Clave primaria autogenerada.
 * @property estudianteId Identificador del estudiante sufragante (EstudianteEntity.id).
 * @property procesoId Identificador del proceso electoral activo (ProcesoElectoralEntity.id).
 * @property fechaHora Marca de tiempo de la emisión de la participación.
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
