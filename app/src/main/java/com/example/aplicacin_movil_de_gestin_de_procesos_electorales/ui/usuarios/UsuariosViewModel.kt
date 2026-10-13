package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.usuarios

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.UsuarioRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class UsuariosUiState(
    val usuarios: List<UsuarioEntity> = emptyList(),
    val instituciones: List<InstitucionEntity> = emptyList(),
    val busquedaTexto: String = "",
    val rolFiltro: String = "Todos", // "Todos", "Administrador", "Rector", "Docente"
    val cargando: Boolean = true,
    val mensajeError: String? = null,
    val mensajeExito: String? = null,
    val mostrarDialogoFormulario: Boolean = false,
    val usuarioEnEdicion: UsuarioEntity? = null,
    val usuarioADesactivar: UsuarioEntity? = null
)

class UsuariosViewModel(
    private val repository: UsuarioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsuariosUiState())
    val uiState: StateFlow<UsuariosUiState> = _uiState.asStateFlow()

    init {
        cargarUsuarios()
        cargarInstituciones()
    }

    private fun cargarUsuarios() {
        viewModelScope.launch {
            repository.obtenerTodosUsuarios()
                .catch { err ->
                    _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al cargar usuarios.") }
                }
                .collect { lista ->
                    _uiState.update { it.copy(usuarios = lista, cargando = false) }
                }
        }
    }

    private fun cargarInstituciones() {
        viewModelScope.launch {
            repository.obtenerTodasInstituciones()
                .catch { emit(emptyList()) }
                .collect { insts ->
                    _uiState.update { it.copy(instituciones = insts) }
                }
        }
    }

    fun onBusquedaChange(texto: String) {
        _uiState.update { it.copy(busquedaTexto = texto) }
    }

    fun onRolFiltroChange(rol: String) {
        _uiState.update { it.copy(rolFiltro = rol) }
    }

    fun onAbrirFormularioNuevo() {
        _uiState.update { it.copy(mostrarDialogoFormulario = true, usuarioEnEdicion = null, mensajeError = null) }
    }

    fun onAbrirFormularioEditar(usuario: UsuarioEntity) {
        _uiState.update { it.copy(mostrarDialogoFormulario = true, usuarioEnEdicion = usuario, mensajeError = null) }
    }

    fun onCerrarFormulario() {
        _uiState.update { it.copy(mostrarDialogoFormulario = false, usuarioEnEdicion = null) }
    }

    fun guardarUsuario(
        nombre: String,
        apellido: String,
        correo: String,
        contrasena: String,
        confirmacion: String,
        rol: String,
        institucionId: Int?,
        estado: Boolean
    ) {
        // Validaciones
        if (nombre.isBlank() || apellido.isBlank() || correo.isBlank() || rol.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Todos los campos obligatorios deben ser completados.") }
            return
        }

        if (!correo.contains("@") || !correo.contains(".")) {
            _uiState.update { it.copy(mensajeError = "Ingrese un correo electrónico válido.") }
            return
        }

        val usuarioEditando = _uiState.value.usuarioEnEdicion
        if (usuarioEditando == null) { // Registro de nuevo usuario
            if (contrasena.isBlank()) {
                _uiState.update { it.copy(mensajeError = "La contraseña es obligatoria para nuevos usuarios.") }
                return
            }
            if (contrasena.length < 6) {
                _uiState.update { it.copy(mensajeError = "La contraseña debe tener al menos 6 caracteres.") }
                return
            }
            if (contrasena != confirmacion) {
                _uiState.update { it.copy(mensajeError = "Las contraseñas no coinciden.") }
                return
            }
        } else { // Edición de usuario
            if (contrasena.isNotBlank()) {
                if (contrasena.length < 6) {
                    _uiState.update { it.copy(mensajeError = "La nueva contraseña debe tener al menos 6 caracteres.") }
                    return
                }
                if (contrasena != confirmacion) {
                    _uiState.update { it.copy(mensajeError = "Las contraseñas no coinciden.") }
                    return
                }
            }
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }
            val resultado = if (usuarioEditando == null) {
                repository.registrarUsuario(nombre, apellido, correo, contrasena, rol, institucionId, estado)
            } else {
                repository.actualizarUsuario(usuarioEditando, nombre, apellido, correo, contrasena.ifBlank { null }, rol, institucionId, estado)
            }

            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mostrarDialogoFormulario = false,
                            usuarioEnEdicion = null,
                            mensajeExito = "Usuario guardado exitosamente."
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(cargando = false, mensajeError = err.message ?: "Error al guardar el usuario.") }
                }
            )
        }
    }

    fun solicitarCambioEstado(usuario: UsuarioEntity) {
        val currentAdmin = SessionManager.currentUser.value
        if (usuario.estado) { // Intento de desactivar
            if (currentAdmin != null && currentAdmin.id == usuario.id) {
                _uiState.update { it.copy(mensajeError = "No puedes desactivar tu propia cuenta activa de administrador.") }
                return
            }

            val adminsActivos = _uiState.value.usuarios.count { it.rol == "Administrador" && it.estado }
            if (usuario.rol == "Administrador" && adminsActivos <= 1) {
                _uiState.update { it.copy(mensajeError = "No se puede desactivar el único administrador activo del sistema.") }
                return
            }

            _uiState.update { it.copy(usuarioADesactivar = usuario) }
        } else { // Reactivar
            viewModelScope.launch {
                repository.cambiarEstado(usuario.id, true)
                _uiState.update { it.copy(mensajeExito = "Usuario reactivado exitosamente.") }
            }
        }
    }

    fun confirmarDesactivacion() {
        val usuario = _uiState.value.usuarioADesactivar ?: return
        viewModelScope.launch {
            repository.cambiarEstado(usuario.id, false)
            _uiState.update { it.copy(usuarioADesactivar = null, mensajeExito = "Usuario desactivado exitosamente.") }
        }
    }

    fun cancelarDesactivacion() {
        _uiState.update { it.copy(usuarioADesactivar = null) }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeExito = null) }
    }

    class Factory(private val repository: UsuarioRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(UsuariosViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return UsuariosViewModel(repository) as T
            }
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
    }
}
