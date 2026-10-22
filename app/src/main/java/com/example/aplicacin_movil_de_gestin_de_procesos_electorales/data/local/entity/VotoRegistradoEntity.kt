package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entidad Room para registrar los votos emitidos de forma totalmente ANÓNIMA.
 * Pertenece a la capa de datos (Data Layer) y mapea la tabla SQLite 'votos_registrados'.
 *
 * RATIONALE DE SEGURIDAD ELECTORAL:
 * Esta tabla NO contiene ninguna clave foránea ni referencia hacia la identidad del estudiante.
 * Garantiza constitucionalmente el secreto absoluto del voto en el sistema democrático.
 *
 * @property id Clave primaria autogenerada del sufragio.
 * @property procesoId Identificador del proceso electoral al que corresponde el voto.
 * @property listaElectoralId Identificador de la lista elegida (Null en caso de Voto Nulo).
 * @property esVotoNulo Bandera indicando si la preferencia marcada fue Voto Nulo.
 * @property fechaHora Marca de tiempo de registro del voto en la urna digital.
 */
@Entity(
    tableName = "votos_registrados",
    foreignKeys = [
        ForeignKey(
            entity = ProcesoElectoralEntity::class,
            parentColumns = ["id"],
            childColumns = ["procesoId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["procesoId"])]
)
data class VotoRegistradoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val procesoId: Int,
    val listaElectoralId: Int? = null,
    val esVotoNulo: Boolean = false,
    val fechaHora: String = ""
)
