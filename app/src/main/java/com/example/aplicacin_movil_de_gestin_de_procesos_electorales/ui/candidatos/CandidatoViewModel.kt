package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.candidatos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.repository.CandidatoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CandidatoUiState(
    val candidatos: List<CandidatoEntity> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

class CandidatoViewModel(
    private val repository: CandidatoRepository
) : ViewModel() {

    // Estado del listado de candidatos
    val uiState = repository.obtenerTodos()
        .map<List<CandidatoEntity>, CandidatoUiState> { candidatos ->
            CandidatoUiState(
                candidatos = candidatos,
                cargando = false
            )
        }
        .catch { error ->
            emit(
                CandidatoUiState(
                    cargando = false,
                    mensajeError = error.message
                        ?: "No se pudieron cargar los candidatos."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = CandidatoUiState()
        )

    // Estado del formulario
    private val _formState = MutableStateFlow(CandidatoFormUiState())

    val formState: StateFlow<CandidatoFormUiState> =
        _formState.asStateFlow()

    // Preparar el formulario para registrar o editar
    fun iniciarFormulario(candidato: CandidatoEntity? = null) {
        _formState.value = CandidatoFormUiState(
            nombres = candidato?.nombres ?: "",
            apellidos = candidato?.apellidos ?: "",
            cargo = candidato?.cargo ?: "",
            foto = candidato?.foto ?: "",
            listaId = candidato?.listaId,
            menuAbierto = false,
            error = null
        )
    }

    fun cambiarNombres(valor: String) {
        _formState.update { it.copy(nombres = valor, error = null) }
    }

    fun cambiarApellidos(valor: String) {
        _formState.update { it.copy(apellidos = valor, error = null) }
    }

    fun cambiarCargo(valor: String) {
        _formState.update { it.copy(cargo = valor, error = null) }
    }

    fun cambiarFoto(ruta: String) {
        //_formState.update { it.copy(foto = valor, error = null) }
        _formState.update { estado -> estado.copy(foto = ruta) }
    }

    fun abrirMenuLista() {
        _formState.update { it.copy(menuAbierto = true) }
    }

    fun cerrarMenuLista() {
        _formState.update { it.copy(menuAbierto = false) }
    }

    fun seleccionarLista(listaId: Int) {
        _formState.update {
            it.copy(
                listaId = listaId,
                menuAbierto = false,
                error = null
            )
        }
    }

    // Validar y guardar en Room
    fun guardarFormulario(
        candidatoExistente: CandidatoEntity?,
        onGuardado: () -> Unit
    ) {
        val formulario = _formState.value

        when {
            formulario.nombres.isBlank() ||
                    formulario.apellidos.isBlank() ||
                    formulario.cargo.isBlank() -> {
                _formState.update {
                    it.copy(
                        error = "Completa nombres, apellidos y cargo."
                    )
                }
            }

            formulario.listaId == null -> {
                _formState.update {
                    it.copy(error = "Selecciona una lista electoral.")
                }
            }

            formulario.listaId != null &&
                    uiState.value.candidatos.any { existente ->
                        existente.listaId == formulario.listaId &&
                                existente.cargo.equals(
                                    formulario.cargo,
                                    ignoreCase = true
                                ) &&
                                existente.id != candidatoExistente?.id
                    } -> {
                _formState.update {
                    it.copy(
                        error = "Esta lista ya tiene un candidato con el cargo de ${formulario.cargo}."
                    )
                }
            }

            else -> {
                viewModelScope.launch {
                    try {
                        val candidato = CandidatoEntity(
                            id = candidatoExistente?.id ?: 0,
                            nombres = formulario.nombres.trim(),
                            apellidos = formulario.apellidos.trim(),
                            cargo = formulario.cargo.trim(),
                            foto = formulario.foto.trim(),
                            listaId = formulario.listaId
                                ?: return@launch
                        )

                        if (candidatoExistente == null) {
                            repository.insertar(candidato)
                        } else {
                            repository.actualizar(candidato)
                        }

                        onGuardado()
                    } catch (error: Exception) {
                        _formState.update {
                            it.copy(
                                error = error.message
                                    ?: "No se pudo guardar el candidato."
                            )
                        }
                    }
                }
            }
        }
    }

    fun eliminar(candidato: CandidatoEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(candidato)
            } catch (_: Exception) {
                // El manejo del error del listado se mejorará después.
            }
        }
    }

    fun abrirMenuCargo() {
        _formState.update {
            it.copy(menuCargoAbierto = true)
        }
    }

    fun cerrarMenuCargo() {
        _formState.update {
            it.copy(menuCargoAbierto = false)
        }
    }

}

class CandidatoViewModelFactory(
    private val repository: CandidatoRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CandidatoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CandidatoViewModel(repository) as T
        }

        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
