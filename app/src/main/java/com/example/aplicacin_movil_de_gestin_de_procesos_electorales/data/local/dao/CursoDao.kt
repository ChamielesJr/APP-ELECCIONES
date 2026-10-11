package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CursoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarCurso(curso: CursoEntity)

    @Update
    suspend fun actualizarCurso(curso: CursoEntity)

    @Delete
    suspend fun eliminarCurso(curso: CursoEntity)

    @Query("SELECT * FROM cursos")
    fun obtenerTodosLosCursos(): Flow<List<CursoEntity>>

    @Query("SELECT * FROM cursos WHERE id = :id")
    suspend fun obtenerCursoPorId(id: Int): CursoEntity?
}