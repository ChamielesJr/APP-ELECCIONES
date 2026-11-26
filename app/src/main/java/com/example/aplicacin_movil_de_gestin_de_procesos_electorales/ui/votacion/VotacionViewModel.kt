package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.votacion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.VotacionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

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

@HiltViewModel
class VotacionViewModel @Inject constructor(
    private val votacionRepository: VotacionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VotacionUiState())
    val uiState: StateFlow<VotacionUiState> = _uiState.asStateFlow()

    private var institucionesJob: Job? = null
    private var cursosJob: Job? = null
    private var estudiantesJob: Job? = null

    init {
        cargarInstitucionesHabilitadas()
    }

    fun cargarInstitucionesHabilitadas() {
        institucionesJob?.cancel()
        institucionesJob = viewModelScope.launch {
            _uiState.update { it.copy(cargando = true) }
            votacionRepository.obtenerInstitucionesConProcesosHabilitados()
                .catch { err ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = err.message ?: "Error al cargar instituciones."
                        )
                    }
                }
                .collect { pares ->
                    _uiState.update { it.copy(institucionesConProcesos = pares, cargando = false) }
                }
        }
    }

    fun seleccionarInstitucion(pair: Pair<InstitucionEntity, ProcesoElectoralEntity>) {
        cursosJob?.cancel()
        estudiantesJob?.cancel()

        _uiState.update {
            it.copy(
                institucionSeleccionada = pair.first,
                procesoSeleccionado = pair.second,
                cursosDisponibles = emptyList(),
                cursoSeleccionado = null,
                estudiantesCurso = emptyList(),
                numeroListaIngresado = "",
                pasoActual = 2,
                mensajeError = null
            )
        }

        cursosJob = viewModelScope.launch {
            votacionRepository.obtenerCursosPorInstitucion(pair.first.id)
                .catch { emit(emptyList()) }
                .collect { todosCursos ->
                    val activos = todosCursos.filter { it.estado }
                    _uiState.update { it.copy(cursosDisponibles = activos) }
                }
        }
    }

    fun seleccionarCurso(curso: CursoEntity) {
        estudiantesJob?.cancel()

        _uiState.update {
            it.copy(
                cursoSeleccionado = curso,
                estudiantesCurso = emptyList(),
                mensajeError = null
            )
        }

        estudiantesJob = viewModelScope.launch {
            votacionRepository.obtenerEstudiantesPorCurso(curso.id)
                .catch { emit(emptyList()) }
                .collect { ests ->
                    val activos = ests.filter { it.estado }
                    _uiState.update { it.copy(estudiantesCurso = activos) }
                }
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
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = "No se encontró ningún estudiante habilitado con el número de lista #$num en este curso."
                    )
                }
                return@launch
            }

            if (est.voto) {
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeError = "El estudiante '${est.nombre} ${est.apellido}' ya registró su voto previamente en este proceso electoral."
                    )
                }
                return@launch
            }

            val listas = votacionRepository.obtenerListasActivas().first()
            val candsList = mutableListOf<CandidatoEntity>()
            for (lista in listas) {
                candsList.addAll(votacionRepository.obtenerCandidatosPorLista(lista.id))
            }

            _uiState.update {
                it.copy(
                    estudianteValidado = est,
                    listasElectorales = listas,
                    candidatos = candsList,
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
            _uiState.update {
                it.copy(mensajeError = "Seleccione una lista electoral o la opción 'VOTO NULO' para continuar.")
            }
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
            _uiState.update {
                it.copy(
                    cargando = true,
                    mostrarConfirmacionDialog = false,
                    mensajeError = null
                )
            }
            val res = votacionRepository.registrarVoto(
                estudiante = est,
                procesoId = proc.id,
                listaId = lista?.id,
                esVotoNulo = nulo
            )

            res.fold(
                onSuccess = {
                    _uiState.update { it.copy(cargando = false, pasoActual = 4) }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = err.message ?: "Error al registrar el voto."
                        )
                    }
                }
            )
        }
    }

    fun regresarPasoAnterior() {
        val paso = _uiState.value.pasoActual
        if (paso > 1) {
            _uiState.update { it.copy(pasoActual = paso - 1, mensajeError = null) }
        }
    }

    fun reiniciarFlujo() {
        cursosJob?.cancel()
        estudiantesJob?.cancel()
        _uiState.value = VotacionUiState()
        cargarInstitucionesHabilitadas()
    }

    fun limpiarMensaje() {
        _uiState.update { it.copy(mensajeError = null) }
    }
}