package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.votacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CursoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.EstudianteDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class VotacionUiState(
    val pasoActual: Int = 1, // 1: Institución, 2: Curso/Paralelo/Jornada/Número, 3: Opciones de Voto, 4: Voto Exitoso
    val institucionesConProcesos: List<Pair<InstitucionEntity, ProcesoElectoralEntity>> = emptyList(),
    val institucionSeleccionada: InstitucionEntity? = null,
    val procesoSeleccionado: ProcesoElectoralEntity? = null,
    val cursosDisponibles: List<CursoEntity> = emptyList(),
    val cursoSeleccionado: CursoEntity? = null,
    val estudiantesCurso: List<EstudianteEntity> = emptyList(),
    val numeroListaIngresado: String = "",
    val estudianteValidado: EstudianteEntity? = null,
    val listasElectorales: List<ListaElectoralEntity> = emptyList(),
    val candidatos: List<CandidatoEntity> = emptyList(),
    val opcionSeleccionada: ListaElectoralEntity? = null, // null + esVotoNulo = Voto Nulo
    val esVotoNulo: Boolean = false,
    val mostrarConfirmacionDialog: Boolean = false,
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class VotacionViewModel(
    private val institucionDao: InstitucionDao,
    private val procesoDao: ProcesoElectoralDao,
    private val cursoDao: CursoDao,
    private val estudianteDao: EstudianteDao,
    private val listaDao: ListaElectoralDao,
    private val candidatoDao: CandidatoDao,
    private val votacionDao: VotacionDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(VotacionUiState())
    val uiState: StateFlow<VotacionUiState> = _uiState.asStateFlow()

    init {
        cargarInstitucionesHabilitadas()
    }

    fun cargarInstitucionesHabilitadas() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            try {
                val insts = institucionDao.obtenerTodasInstituciones().first().filter { it.estado }
                val procs = procesoDao.obtenerHabilitados().first()
                val pares = insts.mapNotNull { inst ->
                    val proc = procs.find { p -> p.institucionId == inst.id }
                    if (proc != null) Pair(inst, proc) else null
                }
                _uiState.update { it.copy(institucionesConProcesos = pares, cargando = false) }
            } catch (err: Exception) {
                _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al cargar instituciones.") }
            }
        }
    }

    fun seleccionarInstitucion(pair: Pair<InstitucionEntity, ProcesoElectoralEntity>) {
        _uiState.update {
            it.copy(
                institucionSeleccionada = pair.first,
                procesoSeleccionado = pair.second,
                pasoActual = 2,
                mensajeError = null
            )
        }

        viewModelScope.launch {
            val todosCursos = cursoDao.obtenerTodosLosCursos().first().filter { c -> c.estado && c.institucionId == pair.first.id }
            _uiState.update { it.copy(cursosDisponibles = todosCursos) }
        }
    }

    fun seleccionarCurso(curso: CursoEntity) {
        _uiState.update { it.copy(cursoSeleccionado = curso, mensajeError = null) }
        viewModelScope.launch {
            val ests = estudianteDao.obtenerEstudiantesPorCurso(curso.id).first().filter { it.estado }
            _uiState.update { it.copy(estudiantesCurso = ests) }
        }
    }

    fun onNumeroListaChange(num: String) {
        _uiState.update { it.copy(numeroListaIngresado = num, mensajeError = null) }
    }

    fun validarEstudianteYContinuar() {
        val curso = _uiState.value.cursoSeleccionado
        val numStr = _uiState.value.numeroListaIngresado.trim()
        val num = numStr.toIntOrNull()
        val proc = _uiState.value.procesoSeleccionado

        if (curso == null) {
            _uiState.update { it.copy(mensajeError = "Por favor seleccione su curso, paralelo y jornada.") }
            return
        }

        if (num == null || num <= 0) {
            _uiState.update { it.copy(mensajeError = "Ingrese o seleccione un número de lista válido.") }
            return
        }

        if (proc == null) {
            _uiState.update { it.copy(mensajeError = "No se ha seleccionado un proceso electoral válido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val ests = _uiState.value.estudiantesCurso
            val est = ests.find { it.numeroLista == num }

            if (est == null) {
                _uiState.update { it.copy(cargando = false, mensajeError = "No se encontró ningún estudiante habilitado con el número de lista #$num en este curso.") }
                return@launch
            }

            // Verificar si ya votó
            val yaVoto = votacionDao.verificarParticipacion(est.id, proc.id)
            if (yaVoto > 0) {
                _uiState.update { it.copy(cargando = false, mensajeError = "El estudiante '${est.nombre} ${est.apellido}' ya registró su voto previamente en este proceso electoral.") }
                return@launch
            }

            // Cargar listas electorales activas
            val listas = listaDao.obtenerActivas()
            val cands = candidatoDao.obtenerTodos().first()

            _uiState.update {
                it.copy(
                    estudianteValidado = est,
                    listasElectorales = listas,
                    candidatos = cands,
                    pasoActual = 3,
                    cargando = false
                )
            }
        }
    }

    fun seleccionarOpcionVoto(lista: ListaElectoralEntity?, nulo: Boolean) {
        _uiState.update {
            it.copy(
                opcionSeleccionada = lista,
                esVotoNulo = nulo,
                mensajeError = null
            )
        }
    }

    fun solicitarConfirmacion() {
        if (!_uiState.value.esVotoNulo && _uiState.value.opcionSeleccionada == null) {
            _uiState.update { it.copy(mensajeError = "Seleccione una lista electoral o la opción 'VOTO NULO' para continuar.") }
            return
        }
        _uiState.update { it.copy(mostrarConfirmacionDialog = true) }
    }

    fun cancelarConfirmacion() {
        _uiState.update { it.copy(mostrarConfirmacionDialog = false) }
    }

    fun confirmarVotoDefinitivo() {
        val est = _uiState.value.estudianteValidado
        val proc = _uiState.value.procesoSeleccionado
        val lista = _uiState.value.opcionSeleccionada
        val nulo = _uiState.value.esVotoNulo

        if (est == null || proc == null) return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mostrarConfirmacionDialog = false, mensajeError = null) }
            try {
                val fechaHora = System.currentTimeMillis().toString()
                val exito = votacionDao.registrarVotoEstudiantil(
                    estudianteId = est.id,
                    procesoId = proc.id,
                    listaElectoralId = lista?.id,
                    esVotoNulo = nulo,
                    fechaHora = fechaHora
                )

                if (exito) {
                    _uiState.update { it.copy(cargando = false, pasoActual = 4) }
                } else {
                    _uiState.update { it.copy(cargando = false, mensajeError = "No se pudo registrar el voto. Intente de nuevo.") }
                }
            } catch (err: Exception) {
                _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al registrar el voto.") }
            }
        }
    }

    fun regresarPasoAnterior() {
        val paso = _uiState.value.pasoActual
        if (paso > 1) {
            _uiState.update { it.copy(pasoActual = paso - 1, mensajeError = null) }
        }
    }

    fun reiniciarFlujo() {
        _uiState.value = VotacionUiState()
        cargarInstitucionesHabilitadas()
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeError = null) }
    }

    class Factory(
        private val institucionDao: InstitucionDao,
        private val procesoDao: ProcesoElectoralDao,
        private val cursoDao: CursoDao,
        private val estudianteDao: EstudianteDao,
        private val listaDao: ListaElectoralDao,
        private val candidatoDao: CandidatoDao,
        private val votacionDao: VotacionDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VotacionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VotacionViewModel(institucionDao, procesoDao, cursoDao, estudianteDao, listaDao, candidatoDao, votacionDao) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
