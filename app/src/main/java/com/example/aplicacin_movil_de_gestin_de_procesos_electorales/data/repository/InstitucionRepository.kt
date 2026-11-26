package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import kotlinx.coroutines.flow.Flow

interface InstitucionRepository {
    fun obtenerTodas(): Flow<List<InstitucionEntity>>
    suspend fun registrarInstitucion(
        nombre: String,
        codigo: String,
        direccion: String,
        telefono: String,
        correo: String,
        distrito: String,
        estado: Boolean
    ): Result<Unit>
    suspend fun actualizarInstitucion(institucion: InstitucionEntity): Result<Unit>
    suspend fun cambiarEstado(id: Int, nuevoEstado: Boolean): Result<Unit>
}

class InstitucionRepositoryImpl(
    private val institucionDao: InstitucionDao,
    private val procesoElectoralDao: ProcesoElectoralDao
) : InstitucionRepository {

    override fun obtenerTodas(): Flow<List<InstitucionEntity>> =
        institucionDao.obtenerTodasInstituciones()

    override suspend fun registrarInstitucion(
        nombre: String,
        codigo: String,
        direccion: String,
        telefono: String,
        correo: String,
        distrito: String,
        estado: Boolean
    ): Result<Unit> {
        val codigoLimpio = codigo.trim().uppercase()
        val nueva = InstitucionEntity(
            nombre = nombre.trim(),
            codigoInstitucional = codigoLimpio,
            direccion = direccion.trim(),
            telefono = telefono.trim(),
            correo = correo.trim().lowercase(),
            distrito = distrito.ifBlank { "13D02" },
            estado = estado,
            fechaRegistro = System.currentTimeMillis().toString()
        )
        institucionDao.insertarInstitucion(nueva)
        return Result.success(Unit)
    }

    override suspend fun actualizarInstitucion(institucion: InstitucionEntity): Result<Unit> {
        institucionDao.actualizarInstitucion(institucion)
        return Result.success(Unit)
    }

    override suspend fun cambiarEstado(id: Int, nuevoEstado: Boolean): Result<Unit> {
        if (!nuevoEstado) {
            val procesoHabilitado = procesoElectoralDao.obtenerHabilitadoPorInstitucion(id)
            if (procesoHabilitado != null) {
                return Result.failure(IllegalStateException("No se puede desactivar la institución porque tiene un proceso electoral HABILITADO actualmente."))
            }
        }
        institucionDao.cambiarEstado(id, nuevoEstado)
        return Result.success(Unit)
    }
}
