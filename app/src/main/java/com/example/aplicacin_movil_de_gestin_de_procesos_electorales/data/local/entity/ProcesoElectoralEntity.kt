package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa un Proceso Electoral Escolar (RF9).
 * Pertenece a la capa de datos (Data Layer) y mapea la tabla SQLite 'procesos_electorales'.
 *
 * @property id Identificador único autogenerado por Room.
 * @property institucionId Clave foránea referenciando a la Institución Educativa asociada (InstitucionEntity.id).
 * @property titulo Título o nombre descriptivo del proceso electoral (ej: "Elecciones Estudiantiles 2026").
 * @property mensajeInstitucional Lema o mensaje oficial mostrado a la comunidad votante.
 * @property fechaInicio Fecha programada para el inicio del sufragio (formato AAAA-MM-DD).
 * @property horaInicio Hora programada para el inicio del sufragio (formato HH:MM).
 * @property fechaFin Fecha programada para el cierre del sufragio (formato AAAA-MM-DD).
 * @property horaFin Hora programada para el cierre del sufragio (formato HH:MM).
 * @property estado Estado operativo del proceso: PROGRAMADO, HABILITADO, SUSPENDIDO, FINALIZADO.
 * @property fechaCreacion Marca de tiempo UNIX de creación del registro.
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
