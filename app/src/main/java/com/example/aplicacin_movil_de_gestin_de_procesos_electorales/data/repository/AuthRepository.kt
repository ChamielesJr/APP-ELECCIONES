package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.PasswordHasher

sealed class AuthResult {
    data class Success(val usuario: UsuarioEntity) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

/**
 * Repositorio de autenticación para la verificación de credenciales de usuario.
 */
class AuthRepository(
    private val usuarioDao: UsuarioDao
) {

    suspend fun authenticate(correo: String, contrasena: String): AuthResult {
        val trimmedCorreo = correo.trim()
        if (trimmedCorreo.isEmpty() || contrasena.isEmpty()) {
            return AuthResult.Error("Debe ingresar correo y contraseña.")
        }

        val usuario = usuarioDao.obtenerUsuarioPorCorreo(trimmedCorreo)
            ?: return AuthResult.Error("Correo electrónico o contraseña incorrectos.")

        if (!usuario.estado) {
            return AuthResult.Error("El usuario se encuentra inactivo. Contacte al administrador.")
        }

        val isPasswordValid = PasswordHasher.verifyPassword(
            password = contrasena.toCharArray(),
            storedHash = usuario.contrasena
        )

        if (!isPasswordValid) {
            return AuthResult.Error("Correo electrónico o contraseña incorrectos.")
        }

        if (!usuario.rol.equals("Administrador", ignoreCase = true)) {
            return AuthResult.Error("Acceso denegado: El usuario no tiene rol de Administrador.")
        }

        return AuthResult.Success(usuario)
    }
}
