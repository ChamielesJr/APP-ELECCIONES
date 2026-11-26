package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import kotlinx.coroutines.flow.Flow

interface CandidatoRepository {
    fun obtenerTodos(): Flow<List<CandidatoEntity>>
    suspend fun insertar(candidato: CandidatoEntity): Long
    suspend fun actualizar(candidato: CandidatoEntity)
    suspend fun eliminar(candidato: CandidatoEntity)
    suspend fun obtenerPorId(id: Int): CandidatoEntity?
    suspend fun obtenerPorLista(listaId: Int): List<CandidatoEntity>
}

class CandidatoRepositoryImpl(
    private val candidatoDao: CandidatoDao
) : CandidatoRepository {

    override fun obtenerTodos(): Flow<List<CandidatoEntity>> {
        return candidatoDao.obtenerTodos()
    }

    override suspend fun insertar(candidato: CandidatoEntity): Long {
        return candidatoDao.insertar(candidato)
    }

    override suspend fun actualizar(candidato: CandidatoEntity) {
        candidatoDao.actualizar(candidato)
    }

    override suspend fun eliminar(candidato: CandidatoEntity) {
        candidatoDao.eliminar(candidato)
    }

    override suspend fun obtenerPorId(id: Int): CandidatoEntity? {
        return candidatoDao.obtenerPorId(id)
    }

    override suspend fun obtenerPorLista(listaId: Int): List<CandidatoEntity> {
        return candidatoDao.obtenerPorLista(listaId)
    }
}
