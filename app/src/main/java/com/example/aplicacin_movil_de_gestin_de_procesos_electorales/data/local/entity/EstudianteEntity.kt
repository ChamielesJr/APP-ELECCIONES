package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "estudiantes",
    foreignKeys = [
        ForeignKey(
            entity = CursoEntity::class,
            parentColumns = ["id"],
            childColumns = ["cursoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["cursoId"])]
)
data class EstudianteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val cedula: String,
    val nombre: String,
    val apellido: String,
    val cursoId: Int, // Relación con la tabla Cursos
    val numeroLista: Int = 0, // Posición/número ordinal del estudiante en la lista de su aula
    val voto: Boolean = false, // Control de participación local
    val estado: Boolean = true // Para habilitar/deshabilitar (RF07)
)
