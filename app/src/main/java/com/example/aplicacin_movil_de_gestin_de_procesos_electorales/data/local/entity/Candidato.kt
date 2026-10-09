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
    //val fotografia: String? = null,
    val foto: String = "",
    val listaElectoralId: Int,
    //val descripcion: String? = null
    val listaId: Int,
)

