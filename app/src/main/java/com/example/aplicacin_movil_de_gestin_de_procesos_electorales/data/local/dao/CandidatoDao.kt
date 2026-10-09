package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CandidatoDao {

    @Insert
    suspend fun insertar(candidato: CandidatoEntity): Long

    @Update
    suspend fun actualizar(candidato: CandidatoEntity)

    @Delete
    suspend fun eliminar(candidato: CandidatoEntity)

    @Query("SELECT * FROM candidato")
    fun obtenerTodos(): Flow<List<CandidatoEntity>>

    // Obtener un candidato por ID
    @Query("SELECT * FROM candidato WHERE id = :id")
    suspend fun obtenerPorId(id: Int): CandidatoEntity?

    // Obtener candidatos pertenecientes a una lista
    @Query("SELECT * FROM candidato WHERE listaId = :listaId ORDER BY apellidos ASC")
    suspend fun obtenerPorLista(listaId: Int): List<CandidatoEntity>

    //@Query("""SELECT * FROM candidato WHERE listaElectoralId = :listaId""")
    //fun obtenerPorLista(listaId: Int): Flow<List<CandidatoEntity>>

    @Query("DELETE FROM candidato")
    suspend fun eliminarTodos()
}