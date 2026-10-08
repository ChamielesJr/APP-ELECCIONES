package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) para la gestión de operaciones de base de datos de Usuarios.
 */
@Dao
interface UsuarioDao {

    /**
     * Inserta un nuevo usuario en la base de datos.
     */
    @Insert
    suspend fun insertarUsuario(usuario: UsuarioEntity)

    /**
     * Actualiza la información de un usuario existente.
     */
    @Update
    suspend fun actualizarUsuario(usuario: UsuarioEntity)

    /**
     * Elimina un usuario de la base de datos.
     */
    @Delete
    suspend fun eliminarUsuario(usuario: UsuarioEntity)

    /**
     * Obtiene una lista de todos los usuarios ordenados alfabéticamente por apellido y luego por nombre.
     * Retorna un Flow para observar cambios en tiempo real.
     */
    @Query("SELECT * FROM usuarios ORDER BY apellido ASC, nombre ASC")
    fun obtenerTodosLosUsuarios(): Flow<List<UsuarioEntity>>

    /**
     * Obtiene un usuario específico según su identificador único (ID).
     */
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity?

    /**
     * Obtiene un usuario por su correo electrónico (útil para validación de duplicados y login).
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo LIMIT 1")
    suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity?

    /**
     * Busca usuarios por coincidencia parcial en el nombre o en el apellido,
     * ordenados alfabéticamente por apellido y nombre.
     */
    @Query("SELECT * FROM usuarios WHERE nombre LIKE '%' || :query || '%' OR apellido LIKE '%' || :query || '%' ORDER BY apellido ASC, nombre ASC")
    fun buscarUsuariosPorNombreOApellido(query: String): Flow<List<UsuarioEntity>>

    /**
     * Cambia el estado (activo/inactivo) de un usuario según su ID.
     */
    @Query("UPDATE usuarios SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: Boolean)

    /**
     * Valida el inicio de sesión buscando un usuario por correo y contraseña que esté activo (estado = 1).
     */
    @Query("SELECT * FROM usuarios WHERE correo = :correo AND contrasena = :contrasena AND estado = 1 LIMIT 1")
    suspend fun iniciarSesion(correo: String, contrasena: String): UsuarioEntity?
}
