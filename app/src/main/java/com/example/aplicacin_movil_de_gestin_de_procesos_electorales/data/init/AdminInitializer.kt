package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.init

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.BuildConfig
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.PasswordHasher

sealed class AdminInitResult {
    data object ReleaseBuild : AdminInitResult()
    data class PendingCredentials(val message: String) : AdminInitResult()
    data object AlreadyExists : AdminInitResult()
    data class Created(val email: String) : AdminInitResult()
}

/**
 * Inicializador exclusivo de desarrollo (debug) para crear la cuenta inicial del Administrador.
 */
object AdminInitializer {

    private fun logInfo(msg: String) {
        try {
            android.util.Log.i("AdminInitializer", msg)
        } catch (_: Throwable) {
            println("AdminInitializer: $msg")
        }
    }

    private fun logWarning(msg: String) {
        try {
            android.util.Log.w("AdminInitializer", msg)
        } catch (_: Throwable) {
            println("AdminInitializer (WARNING): $msg")
        }
    }

    suspend fun initialize(
        usuarioDao: UsuarioDao,
        isDebugBuild: Boolean = BuildConfig.DEBUG,
        devEmail: String = BuildConfig.DEV_ADMIN_EMAIL,
        devPassword: String = BuildConfig.DEV_ADMIN_PASSWORD
    ): AdminInitResult {
        // En compilaciones de release, no se ejecuta ningún mecanismo automático
        if (!isDebugBuild) {
            logInfo("Compilación de Release: Omitiendo inicialización automática de Administrador.")
            return AdminInitResult.ReleaseBuild
        }

        val email = devEmail.trim()
        val password = devPassword

        if (email.isBlank() || password.isBlank()) {
            val msg = "Inicialización de desarrollo pendiente: Configure DEV_ADMIN_EMAIL y DEV_ADMIN_PASSWORD en local.properties"
            logWarning(msg)
            return AdminInitResult.PendingCredentials(msg)
        }

        // Comprobar si ya existe una cuenta con ese correo
        val existingUser = usuarioDao.obtenerUsuarioPorCorreo(email)
        if (existingUser != null) {
            logInfo("El usuario administrador con correo '$email' ya existe en la base de datos.")
            return AdminInitResult.AlreadyExists
        }

        val hashedPassword = PasswordHasher.hashPassword(password.toCharArray())
        val adminUser = UsuarioEntity(
            nombre = "Administrador",
            apellido = "Distrito 13D02",
            correo = email,
            contrasena = hashedPassword,
            rol = "Administrador",
            estado = true,
            fechaRegistro = System.currentTimeMillis().toString()
        )

        usuarioDao.insertarUsuario(adminUser)
        logInfo("Cuenta inicial de Administrador creada exitosamente para desarrollo: $email")
        return AdminInitResult.Created(email)
    }
}
