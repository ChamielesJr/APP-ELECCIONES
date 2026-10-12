package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EleccionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión del proceso electoral en la base de datos Room.
 */
@Dao
interface EleccionDao {

    /**
     * Inserta un nuevo proceso electoral o reemplaza uno existente si hay conflicto.
     * Retorna el ID asignado a la fila insertada.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(eleccion: EleccionEntity): Long

    /**
     * Actualiza la información completa de un proceso electoral existente.
     */
    @Update
    suspend fun actualizar(eleccion: EleccionEntity)

    /**
     * Elimina un proceso electoral de la base de datos.
     */
    @Delete
    suspend fun eliminar(eleccion: EleccionEntity)

    /**
     * Obtiene el listado completo de procesos electorales ordenados del más reciente al más antiguo.
     * Retorna un Flow reactivo para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM elecciones ORDER BY id DESC")
    fun obtenerTodas(): Flow<List<EleccionEntity>>

    /**
     * Busca y obtiene una elección por su identificador único (ID).
     */
    @Query("SELECT * FROM elecciones WHERE id = :id")
    suspend fun obtenerPorId(id: Int): EleccionEntity?

    /**
     * Obtiene la última elección registrada o activa en el sistema.
     * Retorna un Flow que emite la elección actual o null si no hay registros.
     */
    @Query("SELECT * FROM elecciones ORDER BY id DESC LIMIT 1")
    fun obtenerEleccionActual(): Flow<EleccionEntity?>

    /**
     * Actualiza únicamente el estado del proceso electoral ("Planificada", "En curso", "Cerrada", "Finalizada").
     */
    @Query("UPDATE elecciones SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, nuevoEstado: String)

    /**
     * Actualiza la visibilidad de los resultados electorales para la comunidad escolar.
     */
    @Query("UPDATE elecciones SET publicarResultados = :publicar WHERE id = :id")
    suspend fun actualizarPublicarResultados(id: Int, publicar: Boolean)

    /**
     * Habilita o deshabilita la recepción de votos en un proceso electoral específico.
     */
    @Query("UPDATE elecciones SET habilitada = :habilitada WHERE id = :id")
    suspend fun actualizarHabilitada(id: Int, habilitada: Boolean)

    /**
     * Retorna la cantidad total de procesos electorales registrados.
     */
    @Query("SELECT COUNT(*) FROM elecciones")
    suspend fun contarElecciones(): Int
}
