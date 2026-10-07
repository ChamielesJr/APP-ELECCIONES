package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión de operaciones de base de datos de Instituciones Educativas.
 */
@Dao
interface InstitucionDao {

    /**
     * Inserta una nueva institución en la base de datos.
     */
    @Insert
    suspend fun insertarInstitucion(institucion: InstitucionEntity)

    /**
     * Actualiza la información de una institución existente.
     */
    @Update
    suspend fun actualizarInstitucion(institucion: InstitucionEntity)

    /**
     * Elimina una institución de la base de datos.
     */
    @Delete
    suspend fun eliminarInstitucion(institucion: InstitucionEntity)

    /**
     * Obtiene una lista de todas las instituciones ordenadas alfabéticamente por nombre.
     * Retorna un Flow para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM instituciones ORDER BY nombre ASC")
    fun obtenerTodasInstituciones(): Flow<List<InstitucionEntity>>

    /**
     * Obtiene una institución específica según su identificador único (ID).
     */
    @Query("SELECT * FROM instituciones WHERE id = :id")
    suspend fun obtenerInstitucionPorId(id: Int): InstitucionEntity?

    /**
     * Busca instituciones por coincidencia parcial en el nombre, ordenadas alfabéticamente.
     * Retorna un Flow para observar resultados en tiempo real.
     */
    @Query("SELECT * FROM instituciones WHERE nombre LIKE '%' || :query || '%' ORDER BY nombre ASC")
    fun buscarInstitucionesPorNombre(query: String): Flow<List<InstitucionEntity>>

    /**
     * Cambia el estado (activo/inactivo) de una institución según su ID.
     */
    @Query("UPDATE instituciones SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: Boolean)
}
