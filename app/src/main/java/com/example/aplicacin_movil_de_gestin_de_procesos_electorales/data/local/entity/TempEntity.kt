package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * ENTIDAD TEMPORAL TÉCNICA
 *
 * Explicación y justificación:
 * Room requiere obligatoriamente al menos una clase anotada con @Entity en la propiedad 'entities'
 * de la anotación @Database para poder procesar la base de datos y generar el código durante la compilación.
 *
 * Instrucciones para el equipo de desarrollo:
 * 1. Cuando agregues tu Entidad real (ej. InstitucionEntity, UsuarioEntity, etc.) dentro de 'data/local/entity/',
 *    agrégala al arreglo 'entities' en AppDatabase.kt: @Database(entities = [TuEntidad::class, ...], version = 1).
 * 2. Una vez agregada la primera entidad real del sistema, elimina esta clase TempEntity y su referencia en AppDatabase.kt.
 */
@Entity(tableName = "temp_entity")
data class TempEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val description: String = "Entidad temporal para inicialización de Room",
)
