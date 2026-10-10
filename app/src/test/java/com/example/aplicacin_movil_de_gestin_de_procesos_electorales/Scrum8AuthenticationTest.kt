package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.init.AdminInitResult
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.init.AdminInitializer
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.AuthRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.AuthResult
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.PasswordHasher
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Scrum8AuthenticationTest {

    private lateinit var fakeUsuarioDao: FakeUsuarioDao
    private lateinit var authRepository: AuthRepository

    @Before
    fun setUp() {
        fakeUsuarioDao = FakeUsuarioDao()
        authRepository = AuthRepository(fakeUsuarioDao)
        SessionManager.logout()
    }

    // 1. Escenario 1: Administrador con credenciales correctas.
    @Test
    fun test01_AdminConCredencialesCorrectas_AutenticaExitosamente() = runBlocking {
        val passHash = PasswordHasher.hashPassword("Admin1234*".toCharArray())
        val admin = UsuarioEntity(
            id = 1,
            nombre = "Admin",
            apellido = "Distrito",
            correo = "admin@distrito13d02.gob.ec",
            contrasena = passHash,
            rol = "Administrador",
            estado = true,
            fechaRegistro = "123456789"
        )
        fakeUsuarioDao.insertarUsuario(admin)

        val result = authRepository.authenticate("admin@distrito13d02.gob.ec", "Admin1234*")
        assertTrue(result is AuthResult.Success)
        assertEquals("admin@distrito13d02.gob.ec", (result as AuthResult.Success).usuario.correo)
    }

    // 2. Escenario 2: Contraseña incorrecta.
    @Test
    fun test02_ContrasenaIncorrecta_RetornaError() = runBlocking {
        val passHash = PasswordHasher.hashPassword("Admin1234*".toCharArray())
        val admin = UsuarioEntity(
            id = 1,
            nombre = "Admin",
            apellido = "Distrito",
            correo = "admin@distrito13d02.gob.ec",
            contrasena = passHash,
            rol = "Administrador",
            estado = true,
            fechaRegistro = "123456789"
        )
        fakeUsuarioDao.insertarUsuario(admin)

        val result = authRepository.authenticate("admin@distrito13d02.gob.ec", "PasswordInvalida")
        assertTrue(result is AuthResult.Error)
        assertEquals("Correo electrónico o contraseña incorrectos.", (result as AuthResult.Error).message)
    }

    // 3. Escenario 3: Correo inexistente.
    @Test
    fun test03_CorreoInexistente_RetornaError() = runBlocking {
        val result = authRepository.authenticate("noexiste@distrito.gob.ec", "Admin1234*")
        assertTrue(result is AuthResult.Error)
        assertEquals("Correo electrónico o contraseña incorrectos.", (result as AuthResult.Error).message)
    }

    // 4. Escenario 4: Usuario inactivo.
    @Test
    fun test04_UsuarioInactivo_RetornaError() = runBlocking {
        val passHash = PasswordHasher.hashPassword("Admin1234*".toCharArray())
        val userInactivo = UsuarioEntity(
            id = 2,
            nombre = "AdminInactivo",
            apellido = "Distrito",
            correo = "inactivo@distrito13d02.gob.ec",
            contrasena = passHash,
            rol = "Administrador",
            estado = false,
            fechaRegistro = "123456789"
        )
        fakeUsuarioDao.insertarUsuario(userInactivo)

        val result = authRepository.authenticate("inactivo@distrito13d02.gob.ec", "Admin1234*")
        assertTrue(result is AuthResult.Error)
        assertEquals("El usuario se encuentra inactivo. Contacte al administrador.", (result as AuthResult.Error).message)
    }

    // 5. Escenario 5: Usuario con rol Docente.
    @Test
    fun test05_UsuarioRolDocente_RetornaAccesoDenegado() = runBlocking {
        val passHash = PasswordHasher.hashPassword("Docente123*".toCharArray())
        val docente = UsuarioEntity(
            id = 3,
            nombre = "Juan",
            apellido = "Perez",
            correo = "docente@distrito13d02.gob.ec",
            contrasena = passHash,
            rol = "Docente",
            estado = true,
            fechaRegistro = "123456789"
        )
        fakeUsuarioDao.insertarUsuario(docente)

        val result = authRepository.authenticate("docente@distrito13d02.gob.ec", "Docente123*")
        assertTrue(result is AuthResult.Error)
        assertEquals("Acceso denegado: El usuario no tiene rol de Administrador.", (result as AuthResult.Error).message)
    }

    // 6. Escenario 6: Usuario con rol Rector.
    @Test
    fun test06_UsuarioRolRector_RetornaAccesoDenegado() = runBlocking {
        val passHash = PasswordHasher.hashPassword("Rector123*".toCharArray())
        val rector = UsuarioEntity(
            id = 4,
            nombre = "Maria",
            apellido = "Gomez",
            correo = "rector@distrito13d02.gob.ec",
            contrasena = passHash,
            rol = "Rector",
            estado = true,
            fechaRegistro = "123456789"
        )
        fakeUsuarioDao.insertarUsuario(rector)

        val result = authRepository.authenticate("rector@distrito13d02.gob.ec", "Rector123*")
        assertTrue(result is AuthResult.Error)
        assertEquals("Acceso denegado: El usuario no tiene rol de Administrador.", (result as AuthResult.Error).message)
    }

    // 7. Escenario 7: Base de datos sin Administrador.
    @Test
    fun test07_BaseDeDatosSinAdministrador_IntentoDeLoginFalla() = runBlocking {
        assertEquals(0, fakeUsuarioDao.users.size)
        val result = authRepository.authenticate("admin@distrito13d02.gob.ec", "Admin1234*")
        assertTrue(result is AuthResult.Error)
    }

    // 8. Escenario 8: Inicialización ejecutada dos veces (Idempotencia).
    @Test
    fun test08_InicializacionEjecutadaDosVeces_EsIdempotente() = runBlocking {
        val email = "admin@distrito13d02.gob.ec"
        val password = "Admin1234*"

        val firstRun = AdminInitializer.initialize(fakeUsuarioDao, isDebugBuild = true, devEmail = email, devPassword = password)
        assertTrue(firstRun is AdminInitResult.Created)
        assertEquals(1, fakeUsuarioDao.users.size)

        val secondRun = AdminInitializer.initialize(fakeUsuarioDao, isDebugBuild = true, devEmail = email, devPassword = password)
        assertTrue(secondRun is AdminInitResult.AlreadyExists)
        assertEquals(1, fakeUsuarioDao.users.size)
    }

    // 9. Escenario 9: Inicialización sin credenciales configuradas.
    @Test
    fun test09_InicializacionSinCredenciales_NoCreaUsuario() = runBlocking {
        val result = AdminInitializer.initialize(fakeUsuarioDao, isDebugBuild = true, devEmail = "", devPassword = "")
        assertTrue(result is AdminInitResult.PendingCredentials)
        assertEquals(0, fakeUsuarioDao.users.size)
    }

    // 10. Escenario 10: Acceso a rutas protegidas sin autenticación.
    @Test
    fun test10_AccesoSinAutenticacion_SessionManagerInvalido() {
        assertFalse(SessionManager.isAuthenticated.value)
        assertNull(SessionManager.currentUser.value)

        val admin = UsuarioEntity(
            id = 1,
            nombre = "Admin",
            apellido = "Distrito",
            correo = "admin@distrito13d02.gob.ec",
            contrasena = "hash",
            rol = "Administrador",
            estado = true,
            fechaRegistro = "123456789"
        )
        SessionManager.setAuthenticatedUser(admin)

        assertTrue(SessionManager.isAuthenticated.value)
        assertEquals("admin@distrito13d02.gob.ec", SessionManager.currentUser.value?.correo)

        SessionManager.logout()
        assertFalse(SessionManager.isAuthenticated.value)
        assertNull(SessionManager.currentUser.value)
    }

    // 11. Prueba de regresión: Verificación de actualización de sesión y consistencia de estado.
    @Test
    fun test11_RegresionActualizacionSesion_ProcesaEstadoCorrectamente() = runBlocking {
        assertFalse(SessionManager.isAuthenticated.value)

        val passHash = PasswordHasher.hashPassword("Admin1234*".toCharArray())
        val admin = UsuarioEntity(
            id = 10,
            nombre = "AdminRegresion",
            apellido = "Distrito",
            correo = "admin_regresion@distrito13d02.gob.ec",
            contrasena = passHash,
            rol = "Administrador",
            estado = true,
            fechaRegistro = "123456789"
        )
        fakeUsuarioDao.insertarUsuario(admin)

        val result = authRepository.authenticate("admin_regresion@distrito13d02.gob.ec", "Admin1234*")
        assertTrue(result is AuthResult.Success)

        val user = (result as AuthResult.Success).usuario
        SessionManager.setAuthenticatedUser(user)

        assertTrue(SessionManager.isAuthenticated.value)
        assertEquals(user, SessionManager.currentUser.value)
    }
}

class FakeUsuarioDao : UsuarioDao {
    val users = mutableListOf<UsuarioEntity>()

    override suspend fun insertarUsuario(usuario: UsuarioEntity) {
        users.add(usuario)
    }

    override suspend fun actualizarUsuario(usuario: UsuarioEntity) {
        val index = users.indexOfFirst { it.id == usuario.id }
        if (index != -1) users[index] = usuario
    }

    override suspend fun eliminarUsuario(usuario: UsuarioEntity) {
        users.removeIf { it.id == usuario.id }
    }

    override fun obtenerTodosLosUsuarios(): Flow<List<UsuarioEntity>> = flowOf(users)

    override suspend fun obtenerUsuarioPorId(id: Int): UsuarioEntity? {
        return users.find { it.id == id }
    }

    override suspend fun obtenerUsuarioPorCorreo(correo: String): UsuarioEntity? {
        return users.find { it.correo.equals(correo, ignoreCase = true) }
    }

    override fun buscarUsuariosPorNombreOApellido(query: String): Flow<List<UsuarioEntity>> {
        return flowOf(users.filter { it.nombre.contains(query, true) || it.apellido.contains(query, true) })
    }

    override suspend fun cambiarEstado(id: Int, estado: Boolean) {
        val user = users.find { it.id == id }
        if (user != null) {
            val updated = user.copy(estado = estado)
            val idx = users.indexOf(user)
            users[idx] = updated
        }
    }

    override suspend fun iniciarSesion(correo: String, contrasena: String): UsuarioEntity? {
        return users.find { it.correo.equals(correo, true) && it.contrasena == contrasena && it.estado }
    }
}
