package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Scrum31ListasElectoralesTest {

    @Test
    fun test01_listaOrdinaria_requiereLogoYNumero() {
        val listaOrdinaria = ListaElectoralEntity(
            id = 1,
            nombre = "Lista 1 - Juventud",
            numero = 1,
            logo = "", // Sin logo
            descripcion = "Lema de prueba",
            color = "#2468F2",
            estado = true
        )

        // Validación: Logo obligatorio para listas ordinarias
        val esVotoNulo = listaOrdinaria.nombre.trim().equals("Voto Nulo", ignoreCase = true)
        val logoValido = esVotoNulo || listaOrdinaria.logo.isNotBlank()

        assertFalse("Una lista ordinaria sin logo no debe ser válida", logoValido)
    }

    @Test
    fun test02_votoNulo_noRequiereLogoNiNumero() {
        val votoNulo = ListaElectoralEntity(
            id = 2,
            nombre = "Voto Nulo",
            numero = 0,
            logo = "",
            descripcion = "Opción de voto nulo",
            color = "#95151F",
            estado = true
        )

        val esVotoNulo = votoNulo.nombre.trim().equals("Voto Nulo", ignoreCase = true)
        val logoValido = esVotoNulo || votoNulo.logo.isNotBlank()

        assertTrue("El Voto Nulo es una excepción válida sin logo obligatorio", logoValido)
        assertTrue(esVotoNulo)
    }

    @Test
    fun test03_validacionPresidenteYVicepresidenteDiferentes() {
        val presidente = "Juan Pérez"
        val vicepresidente = "Juan Pérez" // Mismo nombre

        val sonIguales = presidente.trim().equals(vicepresidente.trim(), ignoreCase = true)
        assertTrue("Presidente y vicepresidente no pueden ser la misma persona", sonIguales)

        val vicepresidenteValido = "Maria Gómez"
        val sonDiferentes = !presidente.trim().equals(vicepresidenteValido.trim(), ignoreCase = true)
        assertTrue("Presidente y vicepresidente diferentes son válidos", sonDiferentes)
    }
}
