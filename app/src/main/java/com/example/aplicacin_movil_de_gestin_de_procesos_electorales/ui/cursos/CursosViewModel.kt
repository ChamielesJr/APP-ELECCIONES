package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.cursos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CursosViewModel(
    private val repository: CursoRepository
) : ViewModel() {
    val cursosState: StateFlow<List<CursoEntity>> = repository.obtenerTodosLosCursos()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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

    class Factory(private val repository: CursoRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CursosViewModel::class.java)) {
                return CursosViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}