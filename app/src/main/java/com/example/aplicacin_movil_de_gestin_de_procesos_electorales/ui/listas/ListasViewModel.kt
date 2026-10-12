package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ListasViewModel(
    private val listaDao: ListaElectoralDao,
    private val candidatoDao: CandidatoDao
) : ViewModel() {

    private val _busqueda = MutableStateFlow("")
    private val _uiState = MutableStateFlow(ListasUiState())
    val uiState: StateFlow<ListasUiState> = _uiState.asStateFlow()

    init {
        cargarListas()
    }

    private fun cargarListas() {
        viewModelScope.launch {
            combine(
                listaDao.obtenerTodasFlow(),
                candidatoDao.obtenerTodos(),
                _busqueda
            ) { listas: List<ListaElectoralEntity>, candidatos: List<CandidatoEntity>, query: String ->

                val listasConCandidatos = listas.map { lista ->
                    val cands = candidatos.filter { it.listaId == lista.id }
                    val presi = cands.find { it.cargo.contains("Presidente", ignoreCase = true) }
                    val vice = cands.find { it.cargo.contains("Vice", ignoreCase = true) }

                    ListaConCandidatos(
                        lista = lista,
                        presidente = presi?.let { "${it.nombres} ${it.apellidos}" } ?: "No registrado",
                        vicepresidente = vice?.let { "${it.nombres} ${it.apellidos}" } ?: "No registrado"
                    )
                }

                val filtradas = if (query.isBlank()) {
                    listasConCandidatos
                } else {
                    listasConCandidatos.filter { item ->
                        item.lista.nombre.contains(query, ignoreCase = true) ||
                                item.presidente.contains(query, ignoreCase = true) ||
                                item.vicepresidente.contains(query, ignoreCase = true)
                    }
                }

                ListasUiState(
                    listas = filtradas,
                    busquedaTexto = query
                )
            }.collect { stateUpdated ->
                _uiState.value = stateUpdated
            }
        }
    }

    fun onBusquedaChange(nuevoTexto: String) {
        _busqueda.value = nuevoTexto
    }

    fun eliminarLista(lista: ListaElectoralEntity) {
        viewModelScope.launch {
            listaDao.eliminar(lista)
        }
    }
}