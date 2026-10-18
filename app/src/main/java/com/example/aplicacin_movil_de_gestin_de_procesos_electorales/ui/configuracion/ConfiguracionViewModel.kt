package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    private val institucionRepository: InstitucionRepository,
    private val procesoElectoralRepository: ProcesoElectoralRepository,
    private val procesoElectoralDao: ProcesoElectoralDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracionState())
    val uiState: StateFlow<ConfiguracionState> = _uiState.asStateFlow()

    private var procesoExistente: ProcesoElectoralEntity? = null
    private var institucionExistente: InstitucionEntity? = null

    init {
        cargarDatosDesdeRoom()
    }

    fun cargarDatosDesdeRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            val listaProcesos = procesoElectoralRepository.obtenerTodosProcesos().firstOrNull()
            val procesoActual = listaProcesos?.firstOrNull()

            val listaInstituciones = institucionRepository.obtenerTodas().firstOrNull()
            val institucionActual = listaInstituciones?.firstOrNull()

            procesoExistente = procesoActual
            institucionExistente = institucionActual

            _uiState.update { state ->
                state.copy(
                    nombreInstitucion = institucionActual?.nombre ?: state.nombreInstitucion,
                    mensajeInstitucional = procesoActual?.mensajeInstitucional ?: state.mensajeInstitucional,
                    nombreEleccion = procesoActual?.titulo ?: state.nombreEleccion,
                    fechaInicio = procesoActual?.fechaInicio ?: state.fechaInicio,
                    horaInicio = procesoActual?.horaInicio ?: state.horaInicio,
                    fechaFin = procesoActual?.fechaFin ?: state.fechaFin,
                    horaFin = procesoActual?.horaFin ?: state.horaFin,
                    habilitarEleccion = procesoActual?.estado == "HABILITADO",
                    publicarResultados = true
                )
            }
        }
    }

    fun onNombreInstitucionChanged(value: String) {
        _uiState.update { it.copy(nombreInstitucion = value, errorNombreInstitucion = value.isBlank()) }
    }

    fun onMensajeInstitucionalChanged(value: String) {
        if (value.length <= ConfiguracionState.MAX_MENSAJE) {
            _uiState.update { it.copy(mensajeInstitucional = value, errorMensajeInstitucional = value.isBlank()) }
        }
    }

    fun onNombreEleccionChanged(value: String) {
        _uiState.update { it.copy(nombreEleccion = value, errorNombreEleccion = value.isBlank()) }
    }

    fun onEscudoSeleccionado(uri: Uri?) {
        _uiState.update { it.copy(escudoUri = uri, errorEscudo = uri == null) }
    }

    fun onFechaInicioChanged(value: String) { _uiState.update { it.copy(fechaInicio = value) } }
    fun onHoraInicioChanged(value: String) { _uiState.update { it.copy(horaInicio = value) } }
    fun onFechaFinChanged(value: String) { _uiState.update { it.copy(fechaFin = value) } }
    fun onHoraFinChanged(value: String) { _uiState.update { it.copy(horaFin = value) } }
    fun onHabilitarEleccionChanged(value: Boolean) { _uiState.update { it.copy(habilitarEleccion = value) } }
    fun onPublicarResultadosChanged(value: Boolean) { _uiState.update { it.copy(publicarResultados = value) } }

    fun guardarConfiguracion(onExito: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value

        if (state.nombreInstitucion.isBlank() || state.mensajeInstitucional.isBlank() ||
            state.nombreEleccion.isBlank() ||
            state.fechaInicio.isBlank() || state.horaInicio.isBlank() ||
            state.fechaFin.isBlank() || state.horaFin.isBlank()) {
            onError("Todos los campos marcados con (*) son obligatorios")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Guardar o Actualizar la Institución
                val inst = institucionExistente
                if (inst == null) {
                    institucionRepository.registrarInstitucion(
                        nombre = state.nombreInstitucion,
                        codigo = "13D02-INST",
                        direccion = "Dirección por defecto",
                        telefono = "0900000000",
                        correo = "contacto@institucion.edu.ec",
                        distrito = "13D02",
                        estado = true
                    )
                } else {
                    val institucionActualizada = inst.copy(
                        nombre = state.nombreInstitucion
                    )
                    institucionRepository.actualizarInstitucion(institucionActualizada)
                }

                // 2. Guardar o Actualizar el Proceso Electoral
                val estadoNuevo = if (state.habilitarEleccion) "HABILITADO" else "SUSPENDIDO"
                val proceso = procesoExistente

                if (proceso == null) {
                    // Si no existe, creamos uno nuevo en la BD
                    val nuevoProceso = ProcesoElectoralEntity(
                        institucionId = 1,
                        titulo = state.nombreEleccion,
                        mensajeInstitucional = state.mensajeInstitucional,
                        fechaInicio = state.fechaInicio,
                        horaInicio = state.horaInicio,
                        fechaFin = state.fechaFin,
                        horaFin = state.horaFin,
                        estado = estadoNuevo,
                        fechaCreacion = "18/10/2026"
                    )
                    procesoElectoralDao.insertar(nuevoProceso)
                } else {
                    // Si ya existe, actualizamos todos sus datos
                    val procesoActualizado = proceso.copy(
                        titulo = state.nombreEleccion,
                        mensajeInstitucional = state.mensajeInstitucional,
                        fechaInicio = state.fechaInicio,
                        horaInicio = state.horaInicio,
                        fechaFin = state.fechaFin,
                        horaFin = state.horaFin,
                        estado = estadoNuevo
                    )
                    procesoElectoralDao.actualizar(procesoActualizado)
                }

                // 3. Volvemos a leer de la BD para sincronizar las entidades locales (procesoExistente e institucionExistente)
                val listaProcesos = procesoElectoralRepository.obtenerTodosProcesos().firstOrNull()
                procesoExistente = listaProcesos?.firstOrNull()

                val listaInstituciones = institucionRepository.obtenerTodas().firstOrNull()
                institucionExistente = listaInstituciones?.firstOrNull()

                viewModelScope.launch(Dispatchers.Main) {
                    onExito()
                }
            } catch (e: Exception) {
                viewModelScope.launch(Dispatchers.Main) {
                    onError("Error al guardar en la base de datos: ${e.message}")
                }
            }
        }
    }
}