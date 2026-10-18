package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.cursos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CursosViewModel @Inject constructor(
    private val repository: CursoRepository
) : ViewModel() {

    // 1. Variable reactiva para el buscador
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 2. Combinamos la base de datos con el buscador en tiempo real
    val cursosState: StateFlow<List<CursoEntity>> = combine(
        repository.obtenerTodosLosCursos(),
        _searchQuery
    ) { cursos, query ->
        if (query.isBlank()) {
            cursos // Si no hay búsqueda, mostramos todos
        } else {
            cursos.filter { curso ->
                // Filtramos por grado, paralelo o jornada
                curso.grado.contains(query, ignoreCase = true) ||
                        curso.paralelo.contains(query, ignoreCase = true) ||
                        curso.jornada.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // 3. Función para actualizar el texto desde la interfaz
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun agregarCurso(grado: String, paralelo: String, jornada: String, nivel: String) {
        viewModelScope.launch {
            val nuevoCurso = CursoEntity(
                grado = grado,
                paralelo = paralelo,
                jornada = jornada,
                nivel = nivel,
                estado = true
            )
            repository.insertarCurso(nuevoCurso)
        }
    }

    fun eliminarCurso(curso: CursoEntity) {
        viewModelScope.launch {
            repository.eliminarCurso(curso)
        }
    }

    fun actualizarCurso(cursoActualizado: CursoEntity) {
        viewModelScope.launch {
            repository.actualizarCurso(cursoActualizado)
        }
    }
}