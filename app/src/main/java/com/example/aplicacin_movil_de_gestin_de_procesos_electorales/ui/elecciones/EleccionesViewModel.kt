package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.elecciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EleccionesUiState(
    val procesos: List<ProcesoElectoralEntity> = emptyList(),
    val instituciones: List<InstitucionEntity> = emptyList(),
    val filtroEstado: String = "Todos", // "Todos", "PROGRAMADO", "HABILITADO", "SUSPENDIDO", "FINALIZADO"
    val busquedaTexto: String = "",
    val cargando: Boolean = true,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val procesoAccion: ProcesoElectoralEntity? = null,
    val tipoAccion: String? = null // "HABILITAR", "SUSPENDER", "REANUDAR", "FINALIZAR"
)

class EleccionesViewModel(
    private val repository: ProcesoElectoralRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EleccionesUiState())
    val uiState: StateFlow<EleccionesUiState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            repository.obtenerTodosProcesos()
                .catch { err ->
                    _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al cargar procesos.") }
                }
                .collect { lista ->
                    _uiState.update { it.copy(procesos = lista, cargando = false) }
                }
        }

        viewModelScope.launch {
            repository.obtenerTodasInstituciones()
                .catch { emit(emptyList()) }
                .collect { insts ->
                    _uiState.update { it.copy(instituciones = insts) }
                }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(busquedaTexto = texto) }
    }

    fun onFiltroEstadoChange(estado: String) {
        _uiState.update { it.copy(filtroEstado = estado) }
    }

    fun solicitarAccion(proceso: ProcesoElectoralEntity, accion: String) {
        _uiState.update { it.copy(procesoAccion = proceso, tipoAccion = accion) }
    }

    fun cancelarAccion() {
        _uiState.update { it.copy(procesoAccion = null, tipoAccion = null) }
    }

    fun confirmarAccion() {
        val proceso = _uiState.value.procesoAccion ?: return
        val accion = _uiState.value.tipoAccion ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val res = when (accion) {
                "HABILITAR" -> repository.habilitarProceso(proceso)
                "SUSPENDER" -> repository.suspenderProceso(proceso)
                "REANUDAR" -> repository.reanudarProceso(proceso)
                "FINALIZAR" -> repository.finalizarProceso(proceso)
                else -> Result.failure(IllegalArgumentException("Acción desconocida"))
            }

            res.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            procesoAccion = null,
                            tipoAccion = null,
                            mensajeExito = "Proceso actualizado a estado '$accion' exitosamente."
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            procesoAccion = null,
                            tipoAccion = null,
                            mensajeError = err.message ?: "Error al cambiar estado del proceso."
                        )
                    }
                }
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
    }

    class Factory(private val repository: ProcesoElectoralRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EleccionesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return EleccionesViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
