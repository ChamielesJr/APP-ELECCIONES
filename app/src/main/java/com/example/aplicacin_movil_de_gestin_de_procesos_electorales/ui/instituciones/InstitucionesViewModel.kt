package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.instituciones

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InstitucionesUiState(
    val instituciones: List<InstitucionEntity> = emptyList(),
    val busquedaTexto: String = "",
    val cargando: Boolean = true,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val mostrarDialogoFormulario: Boolean = false,
    val institucionEnEdicion: InstitucionEntity? = null,
    val institucionADesactivar: InstitucionEntity? = null
)

class InstitucionesViewModel(
    private val repository: InstitucionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InstitucionesUiState())
    val uiState: StateFlow<InstitucionesUiState> = _uiState.asStateFlow()

    init {
        cargarInstituciones()
    }

    private fun cargarInstituciones() {
        viewModelScope.launch {
            repository.obtenerTodas()
                .catch { err ->
                    _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al cargar instituciones.") }
                }
                .collect { lista ->
                    _uiState.update { it.copy(instituciones = lista, cargando = false) }
                }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(busquedaTexto = texto) }
    }

    fun onAbrirFormularioNuevo() {
        _uiState.update { it.copy(mostrarDialogoFormulario = true, institucionEnEdicion = null, mensajeError = null) }
    }

    fun onAbrirFormularioEditar(institucion: InstitucionEntity) {
        _uiState.update { it.copy(mostrarDialogoFormulario = true, institucionEnEdicion = institucion, mensajeError = null) }
    }

    fun onCerrarFormulario() {
        _uiState.update { it.copy(mostrarDialogoFormulario = false, institucionEnEdicion = null) }
    }

    fun guardarInstitucion(
        nombre: String,
        codigo: String,
        direccion: String,
        telefono: String,
        correo: String,
        distrito: String,
        estado: Boolean
    ) {
        if (nombre.isBlank() || codigo.isBlank()) {
            _uiState.update { it.copy(mensajeError = "El nombre y el código institucional son obligatorios.") }
            return
        }

        val codigoLimpio = codigo.trim().uppercase()
        val duplicado = _uiState.value.instituciones.find {
            it.codigoInstitucional.uppercase() == codigoLimpio && it.id != (_uiState.value.institucionEnEdicion?.id ?: -1)
        }
        if (duplicado != null) {
            _uiState.update { it.copy(mensajeError = "Ya existe una institución registrada con el código '$codigoLimpio'.") }
            return
        }

        if (correo.isNotBlank() && (!correo.contains("@") || !correo.contains("."))) {
            _uiState.update { it.copy(mensajeError = "Ingrese un correo electrónico válido.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val editando = _uiState.value.institucionEnEdicion
            val resultado = if (editando == null) {
                repository.registrarInstitucion(nombre, codigoLimpio, direccion, telefono, correo, distrito, estado)
            } else {
                repository.actualizarInstitucion(
                    editando.copy(
                        nombre = nombre.trim(),
                        codigoInstitucional = codigoLimpio,
                        direccion = direccion.trim(),
                        telefono = telefono.trim(),
                        correo = correo.trim().lowercase(),
                        distrito = distrito.ifBlank { "13D02" },
                        estado = estado
                    )
                )
            }

            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mostrarDialogoFormulario = false,
                            institucionEnEdicion = null,
                            mensajeExito = "Institución guardada exitosamente."
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al guardar la institución.") }
                }
            )
        }
    }

    fun solicitarCambioEstado(institucion: InstitucionEntity) {
        if (institucion.estado) {
            _uiState.update { it.copy(institucionADesactivar = institucion) }
        } else {
            viewModelScope.launch {
                repository.cambiarEstado(institucion.id, true)
                _uiState.update { it.copy(mensajeExito = "Institución reactivada exitosamente.") }
            }
        }
    }

    fun confirmarDesactivacion() {
        val inst = _uiState.value.institucionADesactivar ?: return
        viewModelScope.launch {
            val res = repository.cambiarEstado(inst.id, false)
            res.fold(
                onSuccess = {
                    _uiState.update { it.copy(institucionADesactivar = null, mensajeExito = "Institución desactivada exitosamente.") }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(institucionADesactivar = null, mensajeError = err.message ?: "No se pudo desactivar la institución.") }
                }
            )
        }
    }

    fun cancelarDesactivacion() {
        _uiState.update { it.copy(institucionADesactivar = null) }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
    }

    class Factory(private val repository: InstitucionRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InstitucionesViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InstitucionesViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
