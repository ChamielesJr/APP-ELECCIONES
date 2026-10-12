package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa la configuración y el estado de un Proceso Electoral Escolar.
 *
 * @property id Identificador único de la elección (clave primaria autogenerada).
 * @property titulo Título o nombre descriptivo de la elección escolar.
 * @property mensajeInstitucional Mensaje o lema institucional mostrado a los estudiantes.
 * @property institucionNombre Nombre de la institución educativa asociada.
 * @property fechaInicio Fecha de inicio de las votaciones (formato YYYY-MM-DD o DD/MM/YYYY).
 * @property horaInicio Hora de inicio de las votaciones (formato HH:mm).
 * @property fechaFin Fecha de finalización o cierre de las votaciones.
 * @property horaFin Hora de finalización o cierre de las votaciones.
 * @property estado Estado actual del proceso electoral ("Planificada", "En curso", "Cerrada", "Finalizada").
 * @property habilitada Indica si el proceso de votación está activo/habilitado para recibir votos.
 * @property publicarResultados Indica si los resultados preliminares/finales son públicos para los votantes.
 * @property escudoUri URI o ruta del archivo de imagen para el escudo institucional.
 */
@Entity(tableName = "elecciones")
data class EleccionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val titulo: String,
    val mensajeInstitucional: String = "",
    val institucionNombre: String = "Sistema de Distrito 13D02",
    val fechaInicio: String,
    val horaInicio: String,
    val fechaFin: String,
    val horaFin: String,
    val estado: String = "En curso",
    val habilitada: Boolean = true,
    val publicarResultados: Boolean = false,
    val escudoUri: String = "",
)
