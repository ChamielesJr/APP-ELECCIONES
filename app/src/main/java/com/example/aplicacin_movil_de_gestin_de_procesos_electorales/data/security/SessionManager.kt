package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Gestor centralizado del estado de la sesión de usuario autenticado.
 */
object SessionManager {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<UsuarioEntity?>(null)
    val currentUser: StateFlow<UsuarioEntity?> = _currentUser.asStateFlow()

    fun setAuthenticatedUser(usuario: UsuarioEntity) {
        _currentUser.value = usuario
        _isAuthenticated.value = true
    }

    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
    }
}
