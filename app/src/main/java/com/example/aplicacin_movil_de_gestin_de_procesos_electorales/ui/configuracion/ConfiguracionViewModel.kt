package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfiguracionViewModel(
    private val institucionRepository: InstitucionRepository,
    private val procesoElectoralRepository: ProcesoElectoralRepository
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
            // Usa obtenerTodosProcesos() que sí existe en tu ProcesoElectoralRepository
            val listaProcesos = procesoElectoralRepository.obtenerTodosProcesos().firstOrNull()
            val procesoActual = listaProcesos?.firstOrNull()

            // Usa obtenerTodas() que sí existe en tu InstitucionRepository
            val listaInstituciones = institucionRepository.obtenerTodas().firstOrNull()
            val institucionActual = listaInstituciones?.firstOrNull()

            procesoExistente = procesoActual
            institucionExistente = institucionActual

            _uiState.update { state ->
                state.copy(
                    nombreInstitucion = institucionActual?.nombre ?: "",
                    mensajeInstitucional = procesoActual?.mensajeInstitucional ?: "",
                    nombreEleccion = procesoActual?.titulo ?: "",
                    fechaInicio = procesoActual?.fechaInicio ?: "",
                    horaInicio = procesoActual?.horaInicio ?: "",
                    fechaFin = procesoActual?.fechaFin ?: "",
                    horaFin = procesoActual?.horaFin ?: "",
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
            state.nombreEleccion.isBlank() || state.escudoUri == null ||
            state.fechaInicio.isBlank() || state.horaInicio.isBlank() ||
            state.fechaFin.isBlank() || state.horaFin.isBlank()) {
            onError("Todos los campos marcados con (*) son obligatorios")
            return
        }

        viewModelScope.launch(Dispatchers.IO) {
            try {
                // 1. Guardar/Actualizar la Institución mediante registrartInstitucion/actualizarInstitucion de tu InstitucionRepository
                if (institucionExistente == null) {
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
                    val institucionActualizada = institucionExistente!!.copy(
                        nombre = state.nombreInstitucion
                    )
                    institucionRepository.actualizarInstitucion(institucionActualizada)
                }

                // 2. Gestionar el estado del Proceso Electoral usando las funciones de tu ProcesoElectoralRepository
                val proceso = procesoExistente
                if (proceso != null) {
                    if (state.habilitarEleccion) {
                        if (proceso.estado == "PROGRAMADO" || proceso.estado == "SUSPENDIDO") {
                            procesoElectoralRepository.habilitarProceso(proceso)
                        }
                    } else {
                        if (proceso.estado == "HABILITADO") {
                            procesoElectoralRepository.suspenderProceso(proceso)
                        }
                    }
                }

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