package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ConfiguracionUiState(
    val instituciones: List<InstitucionEntity> = emptyList(),
    val procesosRegistrados: List<ProcesoElectoralEntity> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
)

class ConfiguracionViewModel(
    private val institucionDao: InstitucionDao,
    private val procesoDao: ProcesoElectoralDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            institucionDao.obtenerTodasInstituciones()
                .catch { emit(emptyList()) }
                .collect { insts ->
                    _uiState.update { it.copy(instituciones = insts.filter { i -> i.estado }) }
                }
        }

        viewModelScope.launch {
            procesoDao.obtenerTodos()
                .catch { emit(emptyList()) }
                .collect { procs ->
                    _uiState.update { it.copy(procesosRegistrados = procs, cargando = false) }
                }
        }
    }

    fun crearProceso(
        institucionId: Int,
        titulo: String,
        mensaje: String,
        fechaInicio: String,
        horaInicio: String,
        fechaFin: String,
        horaFin: String
    ) {
        if (titulo.isBlank() || fechaInicio.isBlank() || fechaFin.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Complete el título y las fechas de inicio y finalización.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            try {
                val nuevo = ProcesoElectoralEntity(
                    institucionId = institucionId,
                    titulo = titulo.trim(),
                    mensajeInstitucional = mensaje.trim(),
                    fechaInicio = fechaInicio.trim(),
                    horaInicio = horaInicio.trim().ifBlank { "08:00" },
                    fechaFin = fechaFin.trim(),
                    horaFin = horaFin.trim().ifBlank { "16:00" },
                    estado = "PROGRAMADO",
                    fechaCreacion = System.currentTimeMillis().toString()
                )
                procesoDao.insertar(nuevo)
                _uiState.update {
                    it.copy(
                        cargando = false,
                        mensajeExito = "Proceso electoral creado con estado 'PROGRAMADO'. Diríjase al módulo 'Procesos Electorales' para supervisar su estado."
                    )
                }
            } catch (err: Exception) {
                _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al crear el proceso.") }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
    }

    class Factory(
        private val institucionDao: InstitucionDao,
        private val procesoDao: ProcesoElectoralDao
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ConfiguracionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ConfiguracionViewModel(institucionDao, procesoDao) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
