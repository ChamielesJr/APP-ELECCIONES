package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.PasswordHasher
import kotlinx.coroutines.flow.Flow

interface UsuarioRepository {
    fun obtenerTodosUsuarios(): Flow<List<UsuarioEntity>>
    fun obtenerTodasInstituciones(): Flow<List<InstitucionEntity>>
    suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity?
    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity?
    suspend fun registrarUsuario(
        nombre: String,
        apellido: String,
        correo: String,
        contrasenaPlana: String,
        rol: String,
        institucionId: Int?,
        estado: Boolean
    ): Result<Unit>
    suspend fun actualizarUsuario(
        usuarioExistente: UsuarioEntity,
        nombre: String,
        apellido: String,
        correo: String,
        nuevaContrasenaPlana: String?,
        rol: String,
        institucionId: Int?,
        estado: Boolean
    ): Result<Unit>
    suspend fun cambiarEstado(id: Int, nuevoEstado: Boolean)
}

class UsuarioRepositoryImpl(
    private val usuarioDao: UsuarioDao,
    private val institucionDao: InstitucionDao
) : UsuarioRepository {

    override fun obtenerTodosUsuarios(): Flow<List<UsuarioEntity>> =
        usuarioDao.obtenerTodosLosUsuarios()

    override fun obtenerTodasInstituciones(): Flow<List<InstitucionEntity>> =
        institucionDao.obtenerTodasInstituciones()

    override suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity? =
        usuarioDao.obtenerUsuarioPorId(id)

    override suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity? =
        usuarioDao.obtenerUsuarioPorCorreo(correo)

    override suspend fun registrarUsuario(
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

    override suspend fun actualizarUsuario(
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

    override suspend fun cambiarEstado(id: Int, nuevoEstado: Boolean) {
        usuarioDao.cambiarEstado(id, nuevoEstado)
    }
}
