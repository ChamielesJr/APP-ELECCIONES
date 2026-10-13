package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import kotlinx.coroutines.flow.Flow

class ProcesoElectoralRepository(
    private val procesoDao: ProcesoElectoralDao,
    private val institucionDao: InstitucionDao,
    private val listaDao: ListaElectoralDao,
    private val votacionDao: VotacionDao
) {
    fun obtenerTodosProcesos(): Flow<List<ProcesoElectoralEntity>> =
        procesoDao.obtenerTodos()

    fun obtenerTodasInstituciones(): Flow<List<InstitucionEntity>> =
        institucionDao.obtenerTodasInstituciones()

    suspend fun habilitarProceso(proceso: ProcesoElectoralEntity): Result<Unit> {
        if (proceso.estado != "PROGRAMADO" && proceso.estado != "SUSPENDIDO") {
            return Result.failure(IllegalStateException("Un proceso en estado '${proceso.estado}' no se puede habilitar."))
        }

        // Verificar que existan listas electorales activas
        val listasActivas = listaDao.obtenerActivas()
        if (listasActivas.isEmpty()) {
            return Result.failure(IllegalStateException("No existen listas electorales activas registradas para habilitar el proceso."))
        }

        procesoDao.actualizarEstado(proceso.id, "HABILITADO")
        return Result.success(Unit)
    }

    suspend fun suspenderProceso(proceso: ProcesoElectoralEntity): Result<Unit> {
        if (proceso.estado != "HABILITADO") {
            return Result.failure(IllegalStateException("Solo procesos en estado 'HABILITADO' pueden suspenderse."))
        }
        procesoDao.actualizarEstado(proceso.id, "SUSPENDIDO")
        return Result.success(Unit)
    }

    suspend fun reanudarProceso(proceso: ProcesoElectoralEntity): Result<Unit> {
        if (proceso.estado != "SUSPENDIDO") {
            return Result.failure(IllegalStateException("Solo procesos en estado 'SUSPENDIDO' pueden reanudarse."))
        }
        procesoDao.actualizarEstado(proceso.id, "HABILITADO")
        return Result.success(Unit)
    }

    suspend fun finalizarProceso(proceso: ProcesoElectoralEntity): Result<Unit> {
        if (proceso.estado != "HABILITADO" && proceso.estado != "SUSPENDIDO") {
            return Result.failure(IllegalStateException("Un proceso finalizado no puede ser alterado."))
        }
        procesoDao.actualizarEstado(proceso.id, "FINALIZADO")
        return Result.success(Unit)
    }
}
