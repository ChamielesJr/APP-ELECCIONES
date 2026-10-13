package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AdministrationSection(
    onCursosClick: () -> Unit,
    onEstudiantesClick: () -> Unit,
    onListasClick: () -> Unit,
    onUsuariosClick: () -> Unit,
    onInstitucionesClick: () -> Unit,
    onEleccionesClick: () -> Unit,
    onConfiguracionClick: () -> Unit,
    onResultadosClick: () -> Unit,
    totalUsuarios: Int = 0,
    totalInstituciones: Int = 0,
    totalProcesos: Int = 0,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Título de la sección
        Text(
            text = "Administración del sistema",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color(0xFF0F172A)
            )
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Fila 1: Cursos | Estudiantes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdministrationCard(
                title = "Cursos",
                description = "Consultar y administrar grados, paralelos y jornadas.",
                icon = Icons.Default.School,
                iconColor = Color(0xFF4F46E5),
                iconBackgroundColor = Color(0xFFEEF2FF),
                onClick = onCursosClick,
                modifier = Modifier.weight(1f)
            )

            AdministrationCard(
                title = "Estudiantes",
                description = "Administrar los números de lista habilitados para votar.",
                icon = Icons.Default.Groups,
                iconColor = Color(0xFF16A34A),
                iconBackgroundColor = Color(0xFFF0FDF4),
                onClick = onEstudiantesClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 2: Listas electorales | Usuarios (RF3)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdministrationCard(
                title = "Listas electorales",
                description = "Registrar candidatos, colores, logos y estado de las listas.",
                icon = Icons.AutoMirrored.Filled.Assignment,
                iconColor = Color(0xFFD97706),
                iconBackgroundColor = Color(0xFFFFFBEB),
                onClick = onListasClick,
                modifier = Modifier.weight(1f)
            )

            AdministrationCard(
                title = "Usuarios",
                description = "Administrar los usuarios autorizados ($totalUsuarios registrados).",
                icon = Icons.Default.ManageAccounts,
                iconColor = Color(0xFF0284C7),
                iconBackgroundColor = Color(0xFFF0F9FF),
                onClick = onUsuariosClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 3: Instituciones educativas (RF2) | Procesos electorales (RF9)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdministrationCard(
                title = "Instituciones educativas",
                description = "Consultar y registrar instituciones ($totalInstituciones registradas).",
                icon = Icons.Default.AccountBalance,
                iconColor = Color(0xFF059669),
                iconBackgroundColor = Color(0xFFECFDF5),
                onClick = onInstitucionesClick,
                modifier = Modifier.weight(1f)
            )

            AdministrationCard(
                title = "Procesos electorales",
                description = "Supervisar y controlar elecciones ($totalProcesos procesos).",
                icon = Icons.Default.HowToVote,
                iconColor = Color(0xFF2563EB),
                iconBackgroundColor = Color(0xFFEFF6FF),
                onClick = onEleccionesClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 4: Configuración | Resultados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AdministrationCard(
                title = "Configuración",
                description = "Crear y definir parámetros iniciales de la elección.",
                icon = Icons.Default.Settings,
                iconColor = Color(0xFF7C3AED),
                iconBackgroundColor = Color(0xFFF5F3FF),
                onClick = onConfiguracionClick,
                modifier = Modifier.weight(1f)
            )

            AdministrationCard(
                title = "Resultados",
                description = "Consultar resultados y estadísticas electorales.",
                icon = Icons.Default.BarChart,
                iconColor = Color(0xFFEA580C),
                iconBackgroundColor = Color(0xFFFFF7ED),
                onClick = onResultadosClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
