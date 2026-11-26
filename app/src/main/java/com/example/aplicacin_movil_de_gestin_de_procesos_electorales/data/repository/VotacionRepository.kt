package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CursoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.EstudianteDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

interface VotacionRepository {
    fun obtenerInstitucionesConProcesosHabilitados(): Flow<List<Pair<InstitucionEntity, ProcesoElectoralEntity>>>
    fun obtenerCursosPorInstitucion(institucionId: Int): Flow<List<CursoEntity>>
    fun obtenerEstudiantesPorCurso(cursoId: Int): Flow<List<EstudianteEntity>>
    fun obtenerListasActivas(): Flow<List<ListaElectoralEntity>>
    suspend fun obtenerCandidatosPorLista(listaId: Int): List<CandidatoEntity>
    suspend fun registrarVoto(
        estudiante: EstudianteEntity,
        procesoId: Int,
        listaId: Int?,
        esVotoNulo: Boolean
    ): Result<Unit>
}

class VotacionRepositoryImpl(
    private val institucionDao: InstitucionDao,
    private val procesoDao: ProcesoElectoralDao,
    private val cursoDao: CursoDao,
    private val estudianteDao: EstudianteDao,
    private val listaDao: ListaElectoralDao,
    private val candidatoDao: CandidatoDao,
    private val votacionDao: VotacionDao
) : VotacionRepository {

    override fun obtenerInstitucionesConProcesosHabilitados(): Flow<List<Pair<InstitucionEntity, ProcesoElectoralEntity>>> {
        return combine(
            institucionDao.obtenerTodasInstituciones(),
            procesoDao.obtenerHabilitados()
        ) { instituciones, procesos ->
            val activas = instituciones.filter { it.estado }
            activas.mapNotNull { inst ->
                val proc = procesos.find { p -> p.institucionId == inst.id }
                if (proc != null) Pair(inst, proc) else null
            }
        }
    }

    override fun obtenerCursosPorInstitucion(institucionId: Int): Flow<List<CursoEntity>> {
        return cursoDao.obtenerTodosLosCursos().map { cursos ->
            cursos.filter { it.institucionId == institucionId }
        }
    }

    override fun obtenerEstudiantesPorCurso(cursoId: Int): Flow<List<EstudianteEntity>> {
        return estudianteDao.obtenerEstudiantesPorCurso(cursoId)
    }

    override fun obtenerListasActivas(): Flow<List<ListaElectoralEntity>> {
        return listaDao.observarActivasEnTiempoReal()
    }

    override suspend fun obtenerCandidatosPorLista(listaId: Int): List<CandidatoEntity> {
        return candidatoDao.obtenerPorLista(listaId)
    }

    override suspend fun registrarVoto(
        estudiante: EstudianteEntity,
        procesoId: Int,
        listaId: Int?,
        esVotoNulo: Boolean
    ): Result<Unit> {
        return try {
            val fechaHora = System.currentTimeMillis().toString()
            votacionDao.registrarVotoEstudiantil(
                estudianteId = estudiante.id,
                procesoId = procesoId,
                listaElectoralId = if (esVotoNulo) null else listaId,
                esVotoNulo = esVotoNulo,
                fechaHora = fechaHora
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
