package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EstudianteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarEstudiante(estudiante: EstudianteEntity)

    @Update
    suspend fun actualizarEstudiante(estudiante: EstudianteEntity)

    @Delete
    suspend fun eliminarEstudiante(estudiante: EstudianteEntity)

    @Query("SELECT * FROM estudiantes")
    fun obtenerTodosLosEstudiantes(): Flow<List<EstudianteEntity>>

    @Query("SELECT * FROM estudiantes WHERE cursoId = :cursoId")
    fun obtenerEstudiantesPorCurso(cursoId: Int): Flow<List<EstudianteEntity>>
}