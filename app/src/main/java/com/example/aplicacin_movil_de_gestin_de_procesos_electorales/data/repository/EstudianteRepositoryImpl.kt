package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.EstudianteDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import kotlinx.coroutines.flow.Flow

/**
 * Implementación concreta de [EstudianteRepository] que delega las operaciones
 * de base de datos a [EstudianteDao] utilizando Room.
 */
class EstudianteRepositoryImpl(
    private val estudianteDao: EstudianteDao
) : EstudianteRepository {

    override fun obtenerTodosLosEstudiantes(): Flow<List<EstudianteEntity>> {
        return estudianteDao.obtenerTodosLosEstudiantes()
    }

    override fun obtenerEstudiantesPorCurso(cursoId: Int): Flow<List<EstudianteEntity>> {
        return estudianteDao.obtenerEstudiantesPorCurso(cursoId)
    }

    override suspend fun insertarEstudiante(estudiante: EstudianteEntity) {
        estudianteDao.insertarEstudiante(estudiante)
    }

    override suspend fun actualizarEstudiante(estudiante: EstudianteEntity) {
        estudianteDao.actualizarEstudiante(estudiante)
    }

    override suspend fun eliminarEstudiante(estudiante: EstudianteEntity) {
        estudianteDao.eliminarEstudiante(estudiante)
    }
}
