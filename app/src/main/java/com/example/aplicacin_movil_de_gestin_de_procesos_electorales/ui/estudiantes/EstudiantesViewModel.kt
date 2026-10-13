package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.estudiantes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.EstudianteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

/**
 * Modelo de datos auxiliar que une la información del estudiante con su respectivo curso.
 */
data class EstudianteWithCurso(
    val estudiante: EstudianteEntity,
    val curso: CursoEntity?
)

/**
 * ViewModel para el módulo de Estudiantes.
 * Gestiona el estado reactivo mediante StateFlow y aplica los filtros de búsqueda, grado, paralelo, jornada y estado.
 */
class EstudiantesViewModel(
    private val estudianteRepository: EstudianteRepository,
    private val cursoRepository: CursoRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedGrado = MutableStateFlow<String?>(null)
    val selectedGrado: StateFlow<String?> = _selectedGrado.asStateFlow()

    private val _selectedParalelo = MutableStateFlow<String?>(null)
    val selectedParalelo: StateFlow<String?> = _selectedParalelo.asStateFlow()

    private val _selectedJornada = MutableStateFlow<String?>(null)
    val selectedJornada: StateFlow<String?> = _selectedJornada.asStateFlow()

    private val _selectedEstado = MutableStateFlow<Boolean?>(null)
    val selectedEstado: StateFlow<Boolean?> = _selectedEstado.asStateFlow()

    val cursosState: StateFlow<List<CursoEntity>> = cursoRepository.obtenerTodosLosCursos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val estudiantesState: StateFlow<List<EstudianteWithCurso>> = combine(
        estudianteRepository.obtenerTodosLosEstudiantes(),
        cursoRepository.obtenerTodosLosCursos()
    ) { estudiantes, cursos ->
        Pair(estudiantes, cursos)
    }.combine(
        combine(_searchQuery, _selectedGrado, _selectedParalelo) { q, g, p -> Triple(q, g, p) }
    ) { pair, triple ->
        val estudiantes = pair.first
        val cursos = pair.second
        val query = triple.first
        val grado = triple.second
        val paralelo = triple.third
        Pair(Triple(estudiantes, cursos, query), Pair(grado, paralelo))
    }.combine(
        combine(_selectedJornada, _selectedEstado) { j, e -> Pair(j, e) }
    ) { combined, filters ->
        val estudiantes = combined.first.first
        val cursos = combined.first.second
        val query = combined.first.third
        val grado = combined.second.first
        val paralelo = combined.second.second
        val jornada = filters.first
        val estado = filters.second

        val cursoMap = cursos.associateBy { it.id }

        estudiantes.map { estudiante ->
            EstudianteWithCurso(
                estudiante = estudiante,
                curso = cursoMap[estudiante.cursoId]
            )
        }.filter { item ->
            val curso = item.curso
            val matchQuery = query.isBlank() ||
                item.estudiante.nombre.contains(query, ignoreCase = true) ||
                item.estudiante.apellido.contains(query, ignoreCase = true) ||
                item.estudiante.cedula.contains(query, ignoreCase = true)

            val matchGrado = grado.isNullOrBlank() || grado == "Todos" || curso?.grado == grado
            val matchParalelo = paralelo.isNullOrBlank() || paralelo == "Todos" || curso?.paralelo == paralelo
            val matchJornada = jornada.isNullOrBlank() || jornada == "Todas" || curso?.jornada == jornada
            val matchEstado = estado == null || item.estudiante.estado == estado

            matchQuery && matchGrado && matchParalelo && matchJornada && matchEstado
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateGrado(grado: String?) {
        _selectedGrado.value = grado
    }

    fun updateParalelo(paralelo: String?) {
        _selectedParalelo.value = paralelo
    }

    fun updateJornada(jornada: String?) {
        _selectedJornada.value = jornada
    }

    fun updateEstado(estado: Boolean?) {
        _selectedEstado.value = estado
    }

    fun limpiarFiltros() {
        _searchQuery.value = ""
        _selectedGrado.value = null
        _selectedParalelo.value = null
        _selectedJornada.value = null
        _selectedEstado.value = null
    }

    class Factory(
        private val estudianteRepository: EstudianteRepository,
        private val cursoRepository: CursoRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(EstudiantesViewModel::class.java)) {
                return EstudiantesViewModel(estudianteRepository, cursoRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
