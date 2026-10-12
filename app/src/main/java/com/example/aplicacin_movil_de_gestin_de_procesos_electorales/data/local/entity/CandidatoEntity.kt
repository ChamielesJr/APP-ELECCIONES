package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidato")

data class CandidatoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombres: String,
    val apellidos: String,
    val cargo: String,
    val foto: String = "",
    // Identificador de la lista electoral a la que pertenece.
    val listaId: Int,
)
