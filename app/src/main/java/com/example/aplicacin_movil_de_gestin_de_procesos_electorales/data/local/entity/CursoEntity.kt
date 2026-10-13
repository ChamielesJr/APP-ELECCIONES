package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cursos")
data class CursoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val institucionId: Int = 1, // Relación con la Institución Educativa
    val grado: String,      // Ej: "Primero", "Segundo", "Inicial 1"
    val paralelo: String,   // Ej: "A", "B"
    val jornada: String,    // Ej: "Matutina", "Vespertina"
    val nivel: String,      // Ej: "Básica", "Bachillerato"
    val estado: Boolean = true // Para habilitar/deshabilitar (RF06)
)
