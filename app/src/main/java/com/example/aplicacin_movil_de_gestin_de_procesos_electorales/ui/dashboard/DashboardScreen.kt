package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DashboardScreen(
    onInstitucionesClick: () -> Unit = {},
    onUsuariosClick: () -> Unit = {},
    onCursosClick: () -> Unit = {},
    onEstudiantesClick: () -> Unit = {},
    onEleccionesClick: () -> Unit = {},
    onListasClick: () -> Unit = {},
    onCandidatosClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onResultadosClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Panel Administrador (Dashboard)",
            style = MaterialTheme.typography.headlineSmall,
        )
        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onInstitucionesClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Gestión de Instituciones")
        }

        Button(
            onClick = onUsuariosClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Gestión de Usuarios")
        }

        Button(
            onClick = onCursosClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Gestión de Cursos")
        }

        Button(
            onClick = onEstudiantesClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Gestión de Estudiantes")
        }

        Button(
            onClick = onEleccionesClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Procesos Electorales")
        }

        Button(
            onClick = onListasClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Listas Electorales")
        }

        Button(
            onClick = onCandidatosClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Candidatos")
        }

        Button(
            onClick = onConfiguracionClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Configuración Electoral")
        }

        Button(
            onClick = onResultadosClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(text = "Resultados Electorales")
        }
    }
}
