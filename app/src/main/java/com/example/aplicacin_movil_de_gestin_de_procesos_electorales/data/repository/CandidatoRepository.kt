package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import kotlinx.coroutines.flow.Flow

class CandidatoRepository(
    private val candidatoDao: CandidatoDao
) {

    // Observar todos los candidatos.
    fun obtenerTodos(): Flow<List<CandidatoEntity>> {
        return candidatoDao.obtenerTodos()
    }

    // Registrar un candidato.
    suspend fun insertar(candidato: CandidatoEntity): Long {
        return candidatoDao.insertar(candidato)
    }

    // Actualizar los datos de un candidato.
    suspend fun actualizar(candidato: CandidatoEntity) {
        candidatoDao.actualizar(candidato)
    }

    // Eliminar un candidato.
    suspend fun eliminar(candidato: CandidatoEntity) {
        candidatoDao.eliminar(candidato)
    }

    // Buscar un candidato por su identificador.
    suspend fun obtenerPorId(id: Int): CandidatoEntity? {
        return candidatoDao.obtenerPorId(id)
    }

    // Consultar los candidatos que pertenecen a una lista electoral.
    suspend fun obtenerPorLista(listaId: Int): List<CandidatoEntity> {
        return candidatoDao.obtenerPorLista(listaId)
    }
}
