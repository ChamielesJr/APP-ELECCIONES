package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad Room que representa a un Usuario del sistema (Administrador, Rector o Docente).
 */
@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val apellido: String,
    val correo: String,
    val contrasena: String,
    val rol: String,
    val institucionId: Int? = null,
    val estado: Boolean = true,
    val fechaRegistro: String,
)
