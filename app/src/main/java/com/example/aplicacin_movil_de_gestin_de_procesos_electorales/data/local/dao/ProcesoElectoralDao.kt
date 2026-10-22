package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión persistente de los Procesos Electorales (RF9).
 * Pertenece a la capa de acceso a datos (Data Layer) y ejecuta sentencias SQL reactivas en Room.
 */
@Dao
interface ProcesoElectoralDao {

    /**
     * Inserta un nuevo proceso electoral en la base de datos o reemplaza en conflicto.
     * @param proceso Instancia de [ProcesoElectoralEntity] a registrar.
     * @return El ID autogenerado asignado al nuevo registro.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(proceso: ProcesoElectoralEntity): Long

    /**
     * Actualiza la información parametrizada de un proceso electoral existente.
     * @param proceso Instancia modificada del proceso electoral.
     */
    @Update
    suspend fun actualizar(proceso: ProcesoElectoralEntity)

    /**
     * Elimina un proceso electoral de la base de datos local.
     * @param proceso Instancia a eliminar.
     */
    @Delete
    suspend fun eliminar(proceso: ProcesoElectoralEntity)

    /**
     * Obtiene el listado completo de procesos electorales ordenados del más reciente al más antiguo.
     * @return [Flow] reactivo emitiendo cambios en tiempo real hacia la UI.
     */
    @Query("SELECT * FROM procesos_electorales ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ProcesoElectoralEntity>>

    /**
     * Busca y obtiene un proceso electoral por su identificador único (ID).
     * @param id Identificador clave primaria.
     * @return Instancia del proceso o null si no existe.
     */
    @Query("SELECT * FROM procesos_electorales WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProcesoElectoralEntity?

    /**
     * Obtiene los procesos electorales pertenecientes a una institución específica.
     * @param institucionId ID del plantel educativo.
     * @return [Flow] reactivo con el listado de procesos.
     */
    @Query("SELECT * FROM procesos_electorales WHERE institucionId = :institucionId ORDER BY id DESC")
    fun obtenerPorInstitucion(institucionId: Int): Flow<List<ProcesoElectoralEntity>>

    /**
     * Obtiene únicamente los procesos electorales con estado 'HABILITADO'.
     * @return [Flow] reactivo emitiendo procesos activos en tiempo real.
     */
    @Query("SELECT * FROM procesos_electorales WHERE estado = 'HABILITADO' ORDER BY id DESC")
    fun obtenerHabilitados(): Flow<List<ProcesoElectoralEntity>>

    /**
     * Busca si una institución posee un proceso electoral activo ('HABILITADO').
     * Utilizado para validar el acceso en la votación estudiantil.
     */
    @Query("SELECT * FROM procesos_electorales WHERE institucionId = :institucionId AND estado = 'HABILITADO' LIMIT 1")
    suspend fun obtenerHabilitadoPorInstitucion(institucionId: Int): ProcesoElectoralEntity?

    /**
     * Actualiza el estado operativo del proceso electoral (PROGRAMADO, HABILITADO, SUSPENDIDO, FINALIZADO).
     * @param id Identificador del proceso.
     * @param nuevoEstado Nombre del nuevo estado operativo.
     */
    @Query("UPDATE procesos_electorales SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, nuevoEstado: String)

    /**
     * Emite el conteo dinámico de procesos filtrados por su estado operativo.
     */
    @Query("SELECT COUNT(*) FROM procesos_electorales WHERE estado = :estado")
    fun contarPorEstadoFlow(estado: String): Flow<Int>

    /**
     * Consulta suspendida del conteo de procesos por estado.
     */
    @Query("SELECT COUNT(*) FROM procesos_electorales WHERE estado = :estado")
    suspend fun contarPorEstado(estado: String): Int
}
