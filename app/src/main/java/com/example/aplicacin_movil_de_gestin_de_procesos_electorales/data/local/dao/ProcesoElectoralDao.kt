package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcesoElectoralDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(proceso: ProcesoElectoralEntity): Long

    @Update
    suspend fun actualizar(proceso: ProcesoElectoralEntity)

    @Delete
    suspend fun eliminar(proceso: ProcesoElectoralEntity)

    @Query("SELECT * FROM procesos_electorales ORDER BY id DESC")
    fun obtenerTodos(): Flow<List<ProcesoElectoralEntity>>

    @Query("SELECT * FROM procesos_electorales WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): ProcesoElectoralEntity?

    @Query("SELECT * FROM procesos_electorales WHERE institucionId = :institucionId ORDER BY id DESC")
    fun obtenerPorInstitucion(institucionId: Int): Flow<List<ProcesoElectoralEntity>>

    @Query("SELECT * FROM procesos_electorales WHERE estado = 'HABILITADO' ORDER BY id DESC")
    fun obtenerHabilitados(): Flow<List<ProcesoElectoralEntity>>

    @Query("SELECT * FROM procesos_electorales WHERE institucionId = :institucionId AND estado = 'HABILITADO' LIMIT 1")
    suspend fun obtenerHabilitadoPorInstitucion(institucionId: Int): ProcesoElectoralEntity?

    @Query("UPDATE procesos_electorales SET estado = :nuevoEstado WHERE id = :id")
    suspend fun actualizarEstado(id: Int, nuevoEstado: String)

    @Query("SELECT COUNT(*) FROM procesos_electorales WHERE estado = :estado")
    fun contarPorEstadoFlow(estado: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM procesos_electorales WHERE estado = :estado")
    suspend fun contarPorEstado(estado: String): Int
}
