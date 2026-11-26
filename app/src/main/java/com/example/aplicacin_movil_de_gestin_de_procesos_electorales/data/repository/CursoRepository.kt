package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CursoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import kotlinx.coroutines.flow.Flow

interface CursoRepository {
    fun obtenerTodosLosCursos(): Flow<List<CursoEntity>>
    suspend fun insertarCurso(curso: CursoEntity)
    suspend fun actualizarCurso(curso: CursoEntity)
    suspend fun eliminarCurso(curso: CursoEntity)
}

class CursoRepositoryImpl(
    private val cursoDao: CursoDao
) : CursoRepository {
    override fun obtenerTodosLosCursos(): Flow<List<CursoEntity>> {
        return cursoDao.obtenerTodosLosCursos()
    }

    override suspend fun insertarCurso(curso: CursoEntity) {
        cursoDao.insertarCurso(curso)
    }

    override suspend fun actualizarCurso(curso: CursoEntity) {
        cursoDao.actualizarCurso(curso)
    }

    override suspend fun eliminarCurso(curso: CursoEntity) {
        cursoDao.eliminarCurso(curso)
    }
}
