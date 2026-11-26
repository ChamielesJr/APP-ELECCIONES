package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CandidatoRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ListaElectoralUiState(
    val listas: List<ListaElectoralEntity> = emptyList(),
    val listasDesactivadas: List<ListaElectoralEntity> = emptyList(),
    val cargando: Boolean = true,
    val mensajeError: String? = null
)

@HiltViewModel
class ListaElectoralViewModel @Inject constructor(
    private val repository: ListaElectoralRepository,
    private val candidatoRepository: CandidatoRepository
) : ViewModel() {

    val uiState: StateFlow<ListaElectoralUiState> = repository.observarActivas()
        .map<List<ListaElectoralEntity>, ListaElectoralUiState> { listas ->
            ListaElectoralUiState(
                listas = listas,
                cargando = false
            )
        }
        .catch { error ->
            emit(
                ListaElectoralUiState(
                    cargando = false,
                    mensajeError = error.message
                        ?: "No se pudieron cargar las listas electorales."
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ListaElectoralUiState()
        )

    val listasDesactivadas: StateFlow<List<ListaElectoralEntity>> = repository.observarDesactivadas()
        .catch {
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val candidatos: StateFlow<List<CandidatoEntity>> = candidatoRepository.obtenerTodos()
        .catch {
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun guardar(
        lista: ListaElectoralEntity,
        presidenteNombre: String = "",
        vicepresidenteNombre: String = ""
    ) {
        viewModelScope.launch {
            try {
                if (presidenteNombre.isNotBlank() || vicepresidenteNombre.isNotBlank()) {
                    repository.guardarListaConCandidatos(
                        lista = lista,
                        presidenteNombreCompleto = presidenteNombre,
                        vicepresidenteNombreCompleto = vicepresidenteNombre
                    )
                } else if (lista.id == 0) {
                    repository.insertar(lista)
                } else {
                    repository.actualizar(lista)
                }
            } catch (_: Exception) {
                // Manejo de errores
            }
        }
    }

    fun cambiarEstado(lista: ListaElectoralEntity) {
        viewModelScope.launch {
            try {
                repository.cambiarEstado(
                    id = lista.id,
                    estado = !lista.estado
                )
            } catch (_: Exception) {
                // Manejo de errores
            }
        }
    }

    fun eliminar(lista: ListaElectoralEntity) {
        viewModelScope.launch {
            try {
                repository.eliminar(lista)
            } catch (_: Exception) {
                // Manejo de errores
            }
        }
    }
}
