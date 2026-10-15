package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
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
    context: Context
) : ViewModel() {


    private val db = AppDatabase.getInstance(context)
    private val institucionDao = db.institucionDao()
    private val procesoElectoralDao = db.procesoElectoralDao()

    private val _uiState = MutableStateFlow(ConfiguracionState())
    val uiState: StateFlow<ConfiguracionState> = _uiState.asStateFlow()

    private var procesoExistenteId: Int = 0
    private var institucionExistenteId: Int = 1

    init {
        cargarDatosDesdeRoom()
    }


    fun cargarDatosDesdeRoom() {
        viewModelScope.launch(Dispatchers.IO) {
            // Obtenemos la lista de procesos habilitados
            val listaProcesos: List<ProcesoElectoralEntity>? = procesoElectoralDao.obtenerHabilitados().firstOrNull()
            val procesoActual: ProcesoElectoralEntity? = listaProcesos?.firstOrNull()

            if (procesoActual != null) {
                procesoExistenteId = procesoActual.id
                institucionExistenteId = procesoActual.institucionId

                val institucion: InstitucionEntity? = institucionDao.obtenerInstitucionPorId(procesoActual.institucionId)

                _uiState.update { state ->
                    state.copy(
                        nombreInstitucion = institucion?.nombre ?: "",
                        mensajeInstitucional = procesoActual.mensajeInstitucional,
                        nombreEleccion = procesoActual.titulo,
                        fechaInicio = procesoActual.fechaInicio,
                        horaInicio = procesoActual.horaInicio,
                        fechaFin = procesoActual.fechaFin,
                        horaFin = procesoActual.horaFin,
                        habilitarEleccion = procesoActual.estado == "HABILITADO",
                        publicarResultados = true
                    )
                }
            } else {
                val listaInstituciones: List<InstitucionEntity>? = institucionDao.obtenerTodasInstituciones().firstOrNull()
                val institucion: InstitucionEntity? = listaInstituciones?.firstOrNull()
                if (institucion != null) {
                    institucionExistenteId = institucion.id
                    _uiState.update { it.copy(nombreInstitucion = institucion.nombre) }
                }
            }
        }
    }


    fun onNombreInstitucionChanged(value: String) {
        _uiState.update { it.copy(nombreInstitucion = value, errorNombreInstitucion = value.isBlank()) }
    }

    fun onMensajeInstitucionalChanged(value: String) {
        if (value.length <= ConfiguracionState.MAX_MENSAJE) {
            _uiState.update { it.copy(mensajeInstitucional = value) }
        }
    }

    fun onNombreEleccionChanged(value: String) {
        _uiState.update { it.copy(nombreEleccion = value, errorNombreEleccion = value.isBlank()) }
    }

    fun onEscudoSeleccionado(uri: Uri?) {
        _uiState.update { it.copy(escudoUri = uri) }
    }

    fun onFechaInicioChanged(value: String) { _uiState.update { it.copy(fechaInicio = value) } }
    fun onHoraInicioChanged(value: String) { _uiState.update { it.copy(horaInicio = value) } }
    fun onFechaFinChanged(value: String) { _uiState.update { it.copy(fechaFin = value) } }
    fun onHoraFinChanged(value: String) { _uiState.update { it.copy(horaFin = value) } }
    fun onHabilitarEleccionChanged(value: Boolean) { _uiState.update { it.copy(habilitarEleccion = value) } }
    fun onPublicarResultadosChanged(value: Boolean) { _uiState.update { it.copy(publicarResultados = value) } }

    // Guardar en la base de datos
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
                val fechaActual = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

                // A) Guardar/Actualizar la Institución
                val institucionEntity = InstitucionEntity(
                    id = institucionExistenteId,
                    nombre = state.nombreInstitucion,
                    codigoInstitucional = "13D02-INST",
                    direccion = "Dirección por defecto",
                    telefono = "0900000000",
                    correo = "contacto@institucion.edu.ec",
                    distrito = "13D02",
                    estado = true,
                    fechaRegistro = fechaActual
                )

                val instExistente = institucionDao.obtenerInstitucionPorId(institucionExistenteId)
                if (instExistente == null) {
                    institucionDao.insertarInstitucion(institucionEntity)
                } else {
                    institucionDao.actualizarInstitucion(institucionEntity)
                }


                val estadoProceso = if (state.habilitarEleccion) "HABILITADO" else "PROGRAMADO"

                val procesoEntity = ProcesoElectoralEntity(
                    id = procesoExistenteId,
                    institucionId = institucionExistenteId,
                    titulo = state.nombreEleccion,
                    mensajeInstitucional = state.mensajeInstitucional,
                    fechaInicio = state.fechaInicio,
                    horaInicio = state.horaInicio,
                    fechaFin = state.fechaFin,
                    horaFin = state.horaFin,
                    estado = estadoProceso,
                    fechaCreacion = fechaActual
                )

                if (procesoExistenteId == 0) {
                    val idGenerada = procesoElectoralDao.insertar(procesoEntity)
                    procesoExistenteId = idGenerada.toInt()
                } else {
                    procesoElectoralDao.actualizar(procesoEntity)
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