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

/**
 * Data Access Object (DAO) para las operaciones de Sufragio y Control de Votación Estudiantil.
 * Implementa operaciones atómicas transaccionales en Room para resguardar la consistencia y el secreto del voto.
 */
@Dao
interface VotacionDao {

    /**
     * Inserta la participación de un estudiante en un proceso. Lanza excepción si el estudiante ya votó
     * debido a la restricción de unicidad UNIQUE(estudianteId, procesoId).
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarParticipacion(participacion: ParticipacionEstudianteEntity): Long

    /**
     * Inserta la preferencia electoral de forma totalmente anónima en 'votos_registrados'.
     */
    @Insert
    suspend fun insertarVotoAnonimo(voto: VotoRegistradoEntity): Long

    /**
     * Comprueba si un estudiante registra sufragio previo en un proceso electoral específico.
     * @return 1 si ya participó, 0 si está habilitado.
     */
    @Query("SELECT COUNT(*) FROM participacion_estudiantes WHERE estudianteId = :estudianteId AND procesoId = :procesoId")
    suspend fun verificarParticipacion(estudianteId: Int, procesoId: Int): Int

    /**
     * Consulta auxiliar para validar el estado actual del proceso electoral en Room.
     */
    @Query("SELECT * FROM procesos_electorales WHERE id = :procesoId LIMIT 1")
    suspend fun obtenerProcesoPorId(procesoId: Int): ProcesoElectoralEntity?

    /**
     * Actualiza la bandera de voto local del estudiante en la tabla 'estudiantes'.
     */
    @Query("UPDATE estudiantes SET voto = 1 WHERE id = :estudianteId")
    suspend fun marcarVotoEstudiante(estudianteId: Int)

    /**
     * OPERACIÓN TRANSACCIONAL ATÓMICA DE EMISIÓN DE VOTO.
     * Ejecuta dentro de un único bloque transaccional en SQLite:
     * 1. Validación de estado 'HABILITADO' del proceso.
     * 2. Comprobación de abstención/no voto previo del estudiante.
     * 3. Registro de participación en 'participacion_estudiantes'.
     * 4. Registro anónimo del voto en 'votos_registrados'.
     * 5. Marcado de voto local en 'estudiantes'.
     *
     * @throws IllegalStateException Si el proceso no está habilitado o el estudiante ya votó.
     */
    @Transaction
    suspend fun registrarVotoEstudiantil(
        estudianteId: Int,
        procesoId: Int,
        listaElectoralId: Int?,
        esVotoNulo: Boolean,
        fechaHora: String
    ): Boolean {
        val proceso = obtenerProcesoPorId(procesoId)
            ?: throw IllegalStateException("El proceso electoral especificado no existe.")
        if (proceso.estado != "HABILITADO") {
            throw IllegalStateException("El proceso electoral no se encuentra habilitado para recibir votos.")
        }

        val yaParticipo = verificarParticipacion(estudianteId, procesoId)
        if (yaParticipo > 0) {
            throw IllegalStateException("Este estudiante ya ha registrado su voto previamente en este proceso.")
        }

        insertarParticipacion(
            ParticipacionEstudianteEntity(
                estudianteId = estudianteId,
                procesoId = procesoId,
                fechaHora = fechaHora
            )
        )

        insertarVotoAnonimo(
            VotoRegistradoEntity(
                procesoId = procesoId,
                listaElectoralId = listaElectoralId,
                esVotoNulo = esVotoNulo,
                fechaHora = fechaHora
            )
        )

        marcarVotoEstudiante(estudianteId)

        return true
    }

    /**
     * Obtiene la cantidad total de votos obtenidos por una lista electoral en un proceso.
     */
    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId AND listaElectoralId = :listaId")
    suspend fun obtenerConteoVotosPorLista(procesoId: Int, listaId: Int): Int

    /**
     * Obtiene el total de votos nulos registrados en un proceso electoral.
     */
    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId AND esVotoNulo = 1")
    suspend fun obtenerConteoVotosNulos(procesoId: Int): Int

    /**
     * Obtiene el gran total de sufragios contabilizados en la urna digital para un proceso.
     */
    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId")
    suspend fun obtenerTotalVotosProceso(procesoId: Int): Int

    /**
     * Observa de forma reactiva en tiempo real el total de sufragios emitidos en un proceso.
     */
    @Query("SELECT COUNT(*) FROM votos_registrados WHERE procesoId = :procesoId")
    fun obtenerTotalVotosProcesoFlow(procesoId: Int): Flow<Int>
}
