package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import kotlinx.coroutines.flow.Flow

class ListaElectoralRepository(
    private val listaElectoralDao: ListaElectoralDao
) {
    fun observarTodas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarTodas()
    }

    suspend fun insertar(lista: ListaElectoralEntity): Long {
        return listaElectoralDao.insertar(lista)
    }

    suspend fun actualizar(lista: ListaElectoralEntity) {
        listaElectoralDao.actualizar(lista)
    }

    suspend fun eliminar(lista: ListaElectoralEntity) {
        listaElectoralDao.eliminar(lista)
    }

    suspend fun cambiarEstado(id: Int, estado: Boolean) {
        listaElectoralDao.cambiarEstado(id, estado)
    }

    suspend fun obtenerPorId(id: Int): ListaElectoralEntity? {
        return listaElectoralDao.obtenerPorId(id)
    }
}
