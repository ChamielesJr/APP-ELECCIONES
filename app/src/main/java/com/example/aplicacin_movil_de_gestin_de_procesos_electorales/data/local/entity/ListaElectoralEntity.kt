package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lista_electoral")

data class ListaElectoralEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val numero: Int,
    //val logo: String? = null,
    val descripcion: String? = null,

    val color: String,
    val logo: String = "",
    val estado: Boolean = true,
)