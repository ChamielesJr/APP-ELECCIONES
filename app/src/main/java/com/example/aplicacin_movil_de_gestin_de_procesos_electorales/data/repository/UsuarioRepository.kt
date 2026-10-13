package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.PasswordHasher
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(
    private val usuarioDao: UsuarioDao,
    private val institucionDao: InstitucionDao
) {
    fun obtenerTodosUsuarios(): Flow<List<UsuarioEntity>> =
        usuarioDao.obtenerTodosLosUsuarios()

    fun obtenerTodasInstituciones(): Flow<List<InstitucionEntity>> =
        institucionDao.obtenerTodasInstituciones()

    suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity? =
        usuarioDao.obtenerUsuarioPorId(id)

    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity? =
        usuarioDao.obtenerUsuarioPorCorreo(correo)

    suspend fun registrarUsuario(
        nombre: String,
        apellido: String,
        correo: String,
        contrasenaPlana: String,
        rol: String,
        institucionId: Int?,
        estado: Boolean
    ): Result<Unit> {
        val existente = usuarioDao.obtenerUsuarioPorCorreo(correo.trim().lowercase())
        if (existente != null) {
            return Result.failure(IllegalArgumentException("Ya existe un usuario registrado con el correo '${correo.trim()}'."))
        }

        val passwordHash = PasswordHasher.hashPassword(contrasenaPlana.toCharArray())
        val nuevoUsuario = UsuarioEntity(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            correo = correo.trim().lowercase(),
            contrasena = passwordHash,
            rol = rol,
            institucionId = institucionId,
            estado = estado,
            fechaRegistro = System.currentTimeMillis().toString()
        )
        usuarioDao.insertarUsuario(nuevoUsuario)
        return Result.success(Unit)
    }

    suspend fun actualizarUsuario(
        usuarioExistente: UsuarioEntity,
        nombre: String,
        apellido: String,
        correo: String,
        nuevaContrasenaPlana: String?,
        rol: String,
        institucionId: Int?,
        estado: Boolean
    ): Result<Unit> {
        val correoLimpio = correo.trim().lowercase()
        if (correoLimpio != usuarioExistente.correo.lowercase()) {
            val existente = usuarioDao.obtenerUsuarioPorCorreo(correoLimpio)
            if (existente != null && existente.id != usuarioExistente.id) {
                return Result.failure(IllegalArgumentException("Ya existe otro usuario registrado con el correo '$correoLimpio'."))
            }
        }

        val hashDefinitivo = if (!nuevaContrasenaPlana.isNullOrBlank()) {
            PasswordHasher.hashPassword(nuevaContrasenaPlana.toCharArray())
        } else {
            usuarioExistente.contrasena
        }

        val usuarioActualizado = usuarioExistente.copy(
            nombre = nombre.trim(),
            apellido = apellido.trim(),
            correo = correoLimpio,
            contrasena = hashDefinitivo,
            rol = rol,
            institucionId = institucionId,
            estado = estado
        )
        usuarioDao.actualizarUsuario(usuarioActualizado)
        return Result.success(Unit)
    }

    suspend fun cambiarEstado(id: Int, nuevoEstado: Boolean) {
        usuarioDao.cambiarEstado(id, nuevoEstado)
    }
}
