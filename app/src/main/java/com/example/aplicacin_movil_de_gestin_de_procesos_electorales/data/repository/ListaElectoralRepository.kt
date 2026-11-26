package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import androidx.room.withTransaction
import kotlinx.coroutines.flow.Flow

interface ListaElectoralRepository {
    fun observarTodas(): Flow<List<ListaElectoralEntity>>
    fun observarActivas(): Flow<List<ListaElectoralEntity>>
    fun observarDesactivadas(): Flow<List<ListaElectoralEntity>>
    suspend fun insertar(lista: ListaElectoralEntity): Long
    suspend fun actualizar(lista: ListaElectoralEntity)
    suspend fun eliminar(lista: ListaElectoralEntity)
    suspend fun cambiarEstado(id: Int, estado: Boolean)
    suspend fun obtenerPorId(id: Int): ListaElectoralEntity?
    suspend fun guardarListaConCandidatos(
        lista: ListaElectoralEntity,
        presidenteNombreCompleto: String,
        vicepresidenteNombreCompleto: String
    ): Long
}

class ListaElectoralRepositoryImpl(
    private val listaElectoralDao: ListaElectoralDao,
    private val candidatoDao: CandidatoDao,
    private val database: AppDatabase
) : ListaElectoralRepository {

    override fun observarTodas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarTodas()
    }

    override fun observarActivas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarActivasEnTiempoReal()
    }

    override fun observarDesactivadas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarDesactivadas()
    }

    override suspend fun insertar(lista: ListaElectoralEntity): Long {
        return listaElectoralDao.insertar(lista)
    }

    override suspend fun actualizar(lista: ListaElectoralEntity) {
        listaElectoralDao.actualizar(lista)
    }

    override suspend fun eliminar(lista: ListaElectoralEntity) {
        database.withTransaction {
            candidatoDao.eliminarPorLista(lista.id)
            listaElectoralDao.eliminar(lista)
        }
    }

    override suspend fun cambiarEstado(id: Int, estado: Boolean) {
        listaElectoralDao.cambiarEstado(id, estado)
    }

    override suspend fun obtenerPorId(id: Int): ListaElectoralEntity? {
        return listaElectoralDao.obtenerPorId(id)
    }

    override suspend fun guardarListaConCandidatos(
        lista: ListaElectoralEntity,
        presidenteNombreCompleto: String,
        vicepresidenteNombreCompleto: String
    ): Long {
        val listaId = if (lista.id == 0) {
            listaElectoralDao.insertar(lista).toInt()
        } else {
            listaElectoralDao.actualizar(lista)
            lista.id
        }

        val candidatosExistentes = candidatoDao.obtenerPorLista(listaId)

        val presidenteExistente = candidatosExistentes.firstOrNull {
            it.cargo.equals("Presidente", ignoreCase = true)
        }

        if (presidenteNombreCompleto.isNotBlank()) {
            val (nombres, apellidos) = separarNombreCompleto(presidenteNombreCompleto)
            if (presidenteExistente != null) {
                candidatoDao.actualizar(
                    presidenteExistente.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        cargo = "Presidente"
                    )
                )
            } else {
                candidatoDao.insertar(
                    CandidatoEntity(
                        nombres = nombres,
                        apellidos = apellidos,
                        cargo = "Presidente",
                        listaId = listaId
                    )
                )
            }
        }

        val vicepresidenteExistente = candidatosExistentes.firstOrNull {
            it.cargo.equals("Vicepresidente", ignoreCase = true)
        }

        if (vicepresidenteNombreCompleto.isNotBlank()) {
            val (nombres, apellidos) = separarNombreCompleto(vicepresidenteNombreCompleto)
            if (vicepresidenteExistente != null) {
                candidatoDao.actualizar(
                    vicepresidenteExistente.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        cargo = "Vicepresidente"
                    )
                )
            } else {
                candidatoDao.insertar(
                    CandidatoEntity(
                        nombres = nombres,
                        apellidos = apellidos,
                        cargo = "Vicepresidente",
                        listaId = listaId
                    )
                )
            }
        }

        return listaId.toLong()
    }

    private fun separarNombreCompleto(nombreCompleto: String): Pair<String, String> {
        val partes = nombreCompleto.trim().split("\\s+".toRegex())
        return when {
            partes.isEmpty() -> "" to ""
            partes.size == 1 -> partes[0] to ""
            partes.size == 2 -> partes[0] to partes[1]
            else -> {
                val mitad = partes.size / 2
                val nombres = partes.take(mitad).joinToString(" ")
                val apellidos = partes.drop(mitad).joinToString(" ")
                nombres to apellidos
            }
        }
    }
}
