package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.EstudianteRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val totalCursos: Int = 0,
    val totalEstudiantesHabilitados: Int = 0,
    val totalListasElectorales: Int = 0,
    val totalUsuarios: Int = 0,
    val totalInstituciones: Int = 0,
    val totalProcesos: Int = 0,
    val votosRegistrados: Int = 0,
    val currentUser: UsuarioEntity? = null,
    val snackbarMessage: String? = null,
    val showLogoutDialog: Boolean = false,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val cursoRepository: CursoRepository,
    private val estudianteRepository: EstudianteRepository,
    private val listaElectoralRepository: ListaElectoralRepository,
    private val institucionRepository: InstitucionRepository,
    private val procesoElectoralRepository: ProcesoElectoralRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeSession()
        observeCursos()
        observeEstudiantes()
        observeListasElectorales()
        observeInstituciones()
        observeProcesos()
    }

    private fun observeSession() {
        viewModelScope.launch {
            SessionManager.currentUser.collect { usuario ->
                _uiState.update { it.copy(currentUser = usuario) }
            }
        }
    }

    private fun observeCursos() {
        viewModelScope.launch {
            cursoRepository.obtenerTodosLosCursos()
                .catch { emit(emptyList()) }
                .collect { cursos ->
                    _uiState.update { it.copy(totalCursos = cursos.size) }
                }
        }
    }

    private fun observeEstudiantes() {
        viewModelScope.launch {
            estudianteRepository.obtenerTodosLosEstudiantes()
                .catch { emit(emptyList()) }
                .collect { estudiantes ->
                    val habilitados = estudiantes.count { it.estado }
                    val votos = estudiantes.count { it.voto }
                    _uiState.update {
                        it.copy(
                            totalEstudiantesHabilitados = habilitados,
                            votosRegistrados = votos
                        )
                    }
                }
        }
    }

    private fun observeListasElectorales() {
        viewModelScope.launch {
            listaElectoralRepository.observarActivas()
                .catch { emit(emptyList()) }
                .collect { listas ->
                    _uiState.update { it.copy(totalListasElectorales = listas.size) }
                }
        }
    }

    private fun observeInstituciones() {
        viewModelScope.launch {
            institucionRepository.obtenerTodas()
                .catch { emit(emptyList()) }
                .collect { insts ->
                    _uiState.update { it.copy(totalInstituciones = insts.size) }
                }
        }
    }

    private fun observeProcesos() {
        viewModelScope.launch {
            procesoElectoralRepository.obtenerTodosProcesos()
                .catch { emit(emptyList()) }
                .collect { procesos ->
                    _uiState.update { it.copy(totalProcesos = procesos.size) }
                }
        }
    }

    fun onLogoutRequested() {
        _uiState.update { it.copy(showLogoutDialog = true) }
    }

    fun onDismissLogoutDialog() {
        _uiState.update { it.copy(showLogoutDialog = false) }
    }

    fun confirmLogout(onLogoutSuccess: () -> Unit) {
        _uiState.update { it.copy(showLogoutDialog = false) }
        SessionManager.logout()
        onLogoutSuccess()
    }

    fun onDismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }
}