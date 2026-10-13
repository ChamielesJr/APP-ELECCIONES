package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class Scrum19DashboardTest {

    @Before
    fun setUp() {
        SessionManager.logout()
    }

    @Test
    fun test01_SessionManager_MantieneUsuarioAutenticadoParaElDashboard() {
        assertFalse(SessionManager.isAuthenticated.value)
        assertNull(SessionManager.currentUser.value)

        val admin = UsuarioEntity(
            id = 1,
            nombre = "Anthony",
            apellido = "Chávez",
            correo = "anthony.chavez@distrito13d02.gob.ec",
            contrasena = "hashed_pass",
            rol = "Administrador",
            estado = true,
            fechaRegistro = "123456789"
        )

        SessionManager.setAuthenticatedUser(admin)

        assertTrue(SessionManager.isAuthenticated.value)
        assertEquals("Anthony", SessionManager.currentUser.value?.nombre)
        assertEquals("Chávez", SessionManager.currentUser.value?.apellido)
        assertEquals("anthony.chavez@distrito13d02.gob.ec", SessionManager.currentUser.value?.correo)
    }

    @Test
    fun test02_CierreDeSesion_LimpiaEstadoEnSessionManager() {
        val admin = UsuarioEntity(
            id = 1,
            nombre = "Admin",
            apellido = "Prueba",
            correo = "admin@distrito.gob.ec",
            contrasena = "pass",
            rol = "Administrador",
            estado = true,
            fechaRegistro = "123"
        )
        SessionManager.setAuthenticatedUser(admin)
        assertTrue(SessionManager.isAuthenticated.value)

        SessionManager.logout()

        assertFalse(SessionManager.isAuthenticated.value)
        assertNull(SessionManager.currentUser.value)
    }

    @Test
    fun test03_RutasAdministrativas_DefinidasCorrectamente() {
        assertEquals("login", Screen.Login.route)
        assertEquals("dashboard", Screen.Dashboard.route)
        assertEquals("cursos", Screen.Cursos.route)
        assertEquals("estudiantes", Screen.Estudiantes.route)
        assertEquals("listas", Screen.Listas.route)
        assertEquals("configuracion", Screen.Configuracion.route)
        assertEquals("resultados", Screen.Resultados.route)
    }

    @Test
    fun test04_CalculoDeEstadisticasDesdeEntidadesRoom() {
        val cursos = listOf(
            CursoEntity(id = 1, institucionId = 1, grado = "Primero", paralelo = "A", jornada = "Matutina", nivel = "Bachillerato"),
            CursoEntity(id = 2, institucionId = 1, grado = "Segundo", paralelo = "B", jornada = "Vespertina", nivel = "Bachillerato")
        )

        val estudiantes = listOf(
            EstudianteEntity(1, "1312345678", "Juan", "Pérez", 1, voto = true, estado = true),
            EstudianteEntity(2, "1312345679", "Maria", "López", 1, voto = false, estado = true),
            EstudianteEntity(3, "1312345680", "Pedro", "Gómez", 2, voto = true, estado = false)
        )

        val listas = listOf(
            ListaElectoralEntity(1, "Lista 1 - Cambio", 1, "Descripción", "#3B82F6", "", true),
            ListaElectoralEntity(2, "Lista 2 - Futuro", 2, "Descripción", "#10B981", "", true)
        )

        val totalCursos = cursos.size
        val totalEstudiantesHabilitados = estudiantes.count { it.estado }
        val totalVotosRegistrados = estudiantes.count { it.voto }
        val totalListasElectorales = listas.size

        assertEquals(2, totalCursos)
        assertEquals(2, totalEstudiantesHabilitados)
        assertEquals(2, totalVotosRegistrados)
        assertEquals(2, totalListasElectorales)
    }
}
