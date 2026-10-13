package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ParticipacionEstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.VotoRegistradoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VotacionDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarParticipacion(participacion: ParticipacionEstudianteEntity): Long

    @Insert
    suspend fun insertarVotoAnonimo(voto: VotoRegistradoEntity): Long

    @Query("SELECT COUNT(*) FROM participacion_estudiantes WHERE estudianteId = :estudianteId AND procesoId = :procesoId")
    suspend fun verificarParticipacion(estudianteId: Int, procesoId: Int): Int

    @Query("SELECT * FROM procesos_electorales WHERE id = :procesoId LIMIT 1")
    suspend fun obtenerProcesoPorId(procesoId: Int): ProcesoElectoralEntity?

    @Query("UPDATE estudiantes SET voto = 1 WHERE id = :estudianteId")
    suspend fun marcarVotoEstudiante(estudianteId: Int)

    @Transaction
    suspend fun registrarVotoEstudiantil(
        estudianteId: Int,
        procesoId: Int,
        listaElectoralId: Int?,
        esVotoNulo: Boolean,
        fechaHora: String
    ): Boolean {
        // 1. Validar que el proceso existe y está HABILITADO
        val proceso = obtenerProcesoPorId(procesoId)
            ?: throw IllegalStateException("El proceso electoral especificado no existe.")
        if (proceso.estado != "HABILITADO") {
            throw IllegalStateException("El proceso electoral no se encuentra habilitado para recibir votos.")
        }

        // 2. Validar que el estudiante no haya votado previamente en este proceso
        val yaParticipo = verificarParticipacion(estudianteId, procesoId)
        if (yaParticipo > 0) {
            throw IllegalStateException("Este estudiante ya ha registrado su voto previamente en este proceso.")
        }

        // 3. Insertar registro de participación (Lanza excepción por restricción UNIQUE en caso de duplicados)
        insertarParticipacion(
            ParticipacionEstudianteEntity(
                estudianteId = estudianteId,
                procesoId = procesoId,
                fechaHora = fechaHora
            )
        )

        // 4. Insertar el voto de forma anónima
        insertarVotoAnonimo(
            VotoRegistradoEntity(
                procesoId = procesoId,
                listaElectoralId = listaElectoralId,
                esVotoNulo = esVotoNulo,
                fechaHora = fechaHora
            )
        )

        // 5. Actualizar bandera local en estudiante
        marcarVotoEstudiante(estudianteId)

        return true
    }

    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId AND listaElectoralId = :listaId")
    suspend fun obtenerConteoVotosPorLista(procesoId: Int, listaId: Int): Int

    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId AND esVotoNulo = 1")
    suspend fun obtenerConteoVotosNulos(procesoId: Int): Int

    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId")
    suspend fun obtenerTotalVotosProceso(procesoId: Int): Int

    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId")
    fun obtenerTotalVotosProcesoFlow(procesoId: Int): Flow<Int>
}
