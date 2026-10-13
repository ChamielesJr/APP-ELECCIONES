package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    private val cursoDao = db.cursoDao()
    private val estudianteDao = db.estudianteDao()
    private val listaElectoralDao = db.listaElectoralDao()
    private val usuarioDao = db.usuarioDao()
    private val institucionDao = db.institucionDao()
    private val procesoElectoralDao = db.procesoElectoralDao()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        observeSession()
        observeCursos()
        observeEstudiantes()
        observeListasElectorales()
        observeUsuarios()
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
            cursoDao.obtenerTodosLosCursos()
                .catch { emit(emptyList()) }
                .collect { cursos ->
                    _uiState.update { it.copy(totalCursos = cursos.size) }
                }
        }
    }

    private fun observeEstudiantes() {
        viewModelScope.launch {
            estudianteDao.obtenerTodosLosEstudiantes()
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
            listaElectoralDao.obtenerTodasFlow()
                .catch { emit(emptyList()) }
                .collect { listas ->
                    _uiState.update { it.copy(totalListasElectorales = listas.size) }
                }
        }
    }

    private fun observeUsuarios() {
        viewModelScope.launch {
            usuarioDao.obtenerTodosLosUsuarios()
                .catch { emit(emptyList()) }
                .collect { usuarios ->
                    _uiState.update { it.copy(totalUsuarios = usuarios.size) }
                }
        }
    }

    private fun observeInstituciones() {
        viewModelScope.launch {
            institucionDao.obtenerTodasInstituciones()
                .catch { emit(emptyList()) }
                .collect { insts ->
                    _uiState.update { it.copy(totalInstituciones = insts.size) }
                }
        }
    }

    private fun observeProcesos() {
        viewModelScope.launch {
            procesoElectoralDao.obtenerTodos()
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
