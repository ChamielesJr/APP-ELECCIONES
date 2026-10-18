package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultadosViewModel @Inject constructor(
    private val listaElectoralRepository: ListaElectoralRepository,
    private val procesoElectoralRepository: ProcesoElectoralRepository,
    private val votacionDao: VotacionDao,
    private val candidatoDao: CandidatoDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResultadosState())
    val uiState: StateFlow<ResultadosState> = _uiState.asStateFlow()

    init {
        cargarResultadosReales()
    }

    fun cargarResultadosReales() {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Obtener el proceso electoral activo/habilitado más reciente de la BD
                val listaProcesos = procesoElectoralRepository.obtenerTodosProcesos().firstOrNull() ?: emptyList()
                val procesoActivo = listaProcesos.firstOrNull { it.estado.equals("HABILITADO", ignoreCase = true) }
                    ?: listaProcesos.firstOrNull()

                val procesoId = procesoActivo?.id ?: 1

                // 2. Observar en tiempo real las listas activas y los votos acumulados desde Room
                combine(
                    listaElectoralRepository.observarActivas(),
                    votacionDao.obtenerTotalVotosProcesoFlow(procesoId)
                ) { listasBD, totalVotosBD ->

                    // Conteo de votos nulos registrados en Room
                    val votosNulosCount = votacionDao.obtenerConteoVotosNulos(procesoId)

                    // Mapeo dinámico de cada lista electoral registrada en la base de datos
                    val listaResultadosMutable = mutableListOf<ResultadoListaState>()

                    listasBD.forEachIndexed { index, lista ->
                        val votosListaCount = votacionDao.obtenerConteoVotosPorLista(procesoId, lista.id)

                        // Consultar el candidato que figura como Presidente para la lista
                        val candidatos = candidatoDao.obtenerPorLista(lista.id)
                        val presidente = candidatos.firstOrNull { it.cargo.equals("Presidente", ignoreCase = true) }
                        val presidenteNombre = if (presidente != null) "${presidente.nombres} ${presidente.apellidos}" else "Sin registrar"

                        val porcentaje = if (totalVotosBD > 0) {
                            (votosListaCount.toFloat() / totalVotosBD.toFloat()) * 100f
                        } else 0f

                        val logoUriParsed = if (lista.logo.isNotBlank()) {
                            try { Uri.parse(lista.logo) } catch (_: Exception) { null }
                        } else null

                        listaResultadosMutable.add(
                            ResultadoListaState(
                                posicion = index + 1,
                                listaId = lista.id,
                                nombreLista = lista.nombre,
                                nombrePresidente = presidenteNombre,
                                escudoUri = logoUriParsed,
                                votos = votosListaCount,
                                porcentaje = porcentaje,
                                esVotoEspecial = false
                            )
                        )
                    }

                    // Agregar la fila especial de Voto Nulo al final de los resultados
                    val porcentajeNulos = if (totalVotosBD > 0) {
                        (votosNulosCount.toFloat() / totalVotosBD.toFloat()) * 100f
                    } else 0f

                    listaResultadosMutable.add(
                        ResultadoListaState(
                            posicion = listasBD.size + 1,
                            listaId = -1,
                            nombreLista = "Voto Nulo",
                            nombrePresidente = "Voto especial",
                            escudoUri = null,
                            votos = votosNulosCount,
                            porcentaje = porcentajeNulos,
                            esVotoEspecial = true
                        )
                    )

                    // Ordenar por cantidad de votos de mayor a menor y reasignar posiciones
                    val resultadosOrdenados = listaResultadosMutable
                        .sortedByDescending { it.votos }
                        .mapIndexed { idx, item -> item.copy(posicion = idx + 1) }

                    // Conteo total de opciones (listas activas + opción nula)
                    val opcionesTotal = listasBD.size + 1

                    _uiState.update { state ->
                        state.copy(
                            totalVotos = totalVotosBD,
                            opcionesElectoralesCount = opcionesTotal,
                            resultadosListas = resultadosOrdenados,
                            isLoading = false,
                            errorMensaje = null
                        )
                    }
                }.collect {}
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMensaje = e.message) }
            }
        }
    }
}