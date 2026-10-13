package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz de repositorio para la gestión de datos de Estudiantes.
 * Actúa como capa de abstracción entre la fuente de datos local (DAO) y los ViewModels.
 */
interface EstudianteRepository {
    /** Retorna un flujo observable con la lista completa de estudiantes. */
    fun obtenerTodosLosEstudiantes(): Flow<List<EstudianteEntity>>

    /** Retorna un flujo observable con los estudiantes pertenecientes a un curso específico. */
    fun obtenerEstudiantesPorCurso(cursoId: Int): Flow<List<EstudianteEntity>>

    /** Inserta un nuevo estudiante en la base de datos local. */
    suspend fun insertarEstudiante(estudiante: EstudianteEntity)

    /** Actualiza la información de un estudiante existente. */
    suspend fun actualizarEstudiante(estudiante: EstudianteEntity)

    /** Elimina un estudiante de la base de datos local. */
    suspend fun eliminarEstudiante(estudiante: EstudianteEntity)
}
