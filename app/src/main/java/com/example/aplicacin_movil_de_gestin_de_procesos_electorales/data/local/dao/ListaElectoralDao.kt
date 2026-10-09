package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.*
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ListaElectoralDao {

    @Insert
    //suspend fun insertar(lista: ListaElectoral)
    suspend fun insertar(lista: ListaElectoralEntity): Long

    @Update
    suspend fun actualizar(lista: ListaElectoralEntity)

    @Delete
    suspend fun eliminar(lista: ListaElectoralEntity)

    // Obtener todas las listas
    @Query("SELECT * FROM lista_electoral ORDER BY numero ASC")
    suspend fun obtenerTodas(): List<ListaElectoralEntity>

    // Obtener solamente las listas activas
    @Query("SELECT * FROM lista_electoral WHERE estado = 1 ORDER BY numero ASC")
    suspend fun obtenerActivas(): List<ListaElectoralEntity>

    // Obtener una lista por su ID
    @Query("SELECT * FROM lista_electoral WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ListaElectoralEntity?

    // Activar o desactivar una lista
    @Query("UPDATE lista_electoral SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: Boolean)

    @Query("DELETE FROM lista_electoral")
    suspend fun eliminarTodas()
}
