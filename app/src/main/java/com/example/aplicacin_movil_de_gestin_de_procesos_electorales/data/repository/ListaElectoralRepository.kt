package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import kotlinx.coroutines.flow.Flow
import androidx.room.withTransaction
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase


class ListaElectoralRepository(
    private val listaElectoralDao: ListaElectoralDao,
    private val candidatoDao: CandidatoDao,
    private val database: AppDatabase,
) {
    fun observarTodas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarTodas()
    }

    fun observarActivas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarActivasEnTiempoReal()
    }

    fun observarDesactivadas(): Flow<List<ListaElectoralEntity>> {
        return listaElectoralDao.observarDesactivadas()
    }

    suspend fun insertar(lista: ListaElectoralEntity): Long {
        return listaElectoralDao.insertar(lista)
    }

    suspend fun actualizar(lista: ListaElectoralEntity) {
        listaElectoralDao.actualizar(lista)
    }

    suspend fun eliminar(lista: ListaElectoralEntity) {
        database.withTransaction {
            candidatoDao.eliminarPorLista(lista.id)
            listaElectoralDao.eliminar(lista)
        }
    }

    suspend fun cambiarEstado(id: Int, estado: Boolean) {
        listaElectoralDao.cambiarEstado(id, estado)
    }

    suspend fun obtenerPorId(id: Int): ListaElectoralEntity? {
        return listaElectoralDao.obtenerPorId(id)
    }

    suspend fun guardarListaConCandidatos(
        lista: ListaElectoralEntity,
        presidenteNombreCompleto: String,
        vicepresidenteNombreCompleto: String
    ): Long {

        // 1. Guardar o actualizar la lista electoral
        val listaId = if (lista.id == 0) {
            listaElectoralDao.insertar(lista).toInt()
        } else {
            listaElectoralDao.actualizar(lista)
            lista.id
        }

        // 2. Consultar los candidatos que pertenecen a esta lista
        val candidatosExistentes = candidatoDao.obtenerPorLista(listaId)

        // 3. Buscar al presidente por su cargo
        val presidenteExistente = candidatosExistentes.firstOrNull {
            it.cargo.equals("Presidente", ignoreCase = true)
        }

        if (presidenteNombreCompleto.isNotBlank()) {
            val (nombres, apellidos) =
                separarNombreCompleto(presidenteNombreCompleto)

            if (presidenteExistente != null) {
                // Actualizar al presidente existente
                candidatoDao.actualizar(
                    presidenteExistente.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        cargo = "Presidente"
                    )
                )
            } else {
                // Registrar un presidente nuevo
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

        // 4. Buscar al vicepresidente por su cargo
        val vicepresidenteExistente = candidatosExistentes.firstOrNull {
            it.cargo.equals("Vicepresidente", ignoreCase = true)
        }

        if (vicepresidenteNombreCompleto.isNotBlank()) {
            val (nombres, apellidos) =
                separarNombreCompleto(vicepresidenteNombreCompleto)

            if (vicepresidenteExistente != null) {
                // Actualizar al vicepresidente existente
                candidatoDao.actualizar(
                    vicepresidenteExistente.copy(
                        nombres = nombres,
                        apellidos = apellidos,
                        cargo = "Vicepresidente"
                    )
                )
            } else {
                // Registrar un vicepresidente nuevo
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

        // 5. Devolver el identificador de la lista
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
