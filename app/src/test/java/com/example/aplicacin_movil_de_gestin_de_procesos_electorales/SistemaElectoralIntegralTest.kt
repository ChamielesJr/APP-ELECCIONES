package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.PasswordHasher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SistemaElectoralIntegralTest {

    @Test
    fun test_PasswordHasher_HashYVerificacionPBKDF2() {
        val rawPass = "Admin2026Secure!"
        val hash = PasswordHasher.hashPassword(rawPass.toCharArray())

        assertTrue(hash.contains(":"))
        assertTrue(PasswordHasher.verifyPassword(rawPass.toCharArray(), hash))
        assertFalse(PasswordHasher.verifyPassword("WrongPassword".toCharArray(), hash))
    }

    @Test
    fun test_RF3_UsuarioEntity_CreacionYPropiedades() {
        val user = UsuarioEntity(
            id = 1,
            nombre = "Carlos",
            apellido = "Mendoza",
            correo = "carlos.mendoza@distrito13d02.gob.ec",
            contrasena = "hashed_pass_pbkdf2",
            rol = "Docente",
            institucionId = 2,
            estado = true,
            fechaRegistro = "1700000000"
        )

        assertEquals("Carlos", user.nombre)
        assertEquals("Mendoza", user.apellido)
        assertEquals("Docente", user.rol)
        assertTrue(user.estado)
    }

    @Test
    fun test_RF2_InstitucionEntity_AtributosYDistrito() {
        val inst = InstitucionEntity(
            id = 10,
            nombre = "Unidad Educativa Manabí",
            codigoInstitucional = "13H00123",
            direccion = "Av. Metropolitana",
            telefono = "052345678",
            correo = "contacto@uemanabi.edu.ec",
            distrito = "13D02",
            estado = true,
            fechaRegistro = "1700000000"
        )

        assertEquals("13H00123", inst.codigoInstitucional)
        assertEquals("13D02", inst.distrito)
        assertTrue(inst.estado)
    }

    @Test
    fun test_RF9_ProcesoElectoralEntity_EstadosTransiciones() {
        val proc = ProcesoElectoralEntity(
            id = 1,
            institucionId = 10,
            titulo = "Elecciones Estudiantiles 2026",
            mensajeInstitucional = "Líderes del Futuro",
            fechaInicio = "2026-11-01",
            horaInicio = "08:00",
            fechaFin = "2026-11-01",
            horaFin = "16:00",
            estado = "PROGRAMADO"
        )

        assertEquals("PROGRAMADO", proc.estado)

        val habilitado = proc.copy(estado = "HABILITADO")
        assertEquals("HABILITADO", habilitado.estado)

        val suspendido = habilitado.copy(estado = "SUSPENDIDO")
        assertEquals("SUSPENDIDO", suspendido.estado)

        val finalizado = suspendido.copy(estado = "FINALIZADO")
        assertEquals("FINALIZADO", finalizado.estado)
    }

    @Test
    fun test_EstudianteEntity_NumeroLista() {
        val est = EstudianteEntity(
            id = 1,
            cedula = "1315554443",
            nombre = "Alejandro",
            apellido = "Zambrano",
            cursoId = 5,
            numeroLista = 12,
            voto = false,
            estado = true
        )

        assertEquals(12, est.numeroLista)
        assertFalse(est.voto)
    }
}
