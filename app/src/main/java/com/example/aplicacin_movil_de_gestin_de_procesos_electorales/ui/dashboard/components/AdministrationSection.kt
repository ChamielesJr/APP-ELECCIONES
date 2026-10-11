package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
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
    onConfiguracionClick: () -> Unit,
    onResultadosClick: () -> Unit,
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

        // Fila 2: Listas electorales | Configuración
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
                title = "Configuración",
                description = "Configurar el nombre, mensaje institucional, fechas y horarios de la elección.",
                icon = Icons.Default.Settings,
                iconColor = Color(0xFF7C3AED),
                iconBackgroundColor = Color(0xFFF5F3FF),
                onClick = onConfiguracionClick,
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 3: Resultados (Tarjeta agregada según especificación de SCRUM-19)
        AdministrationCard(
            title = "Resultados",
            description = "Consultar los resultados y estadísticas del proceso electoral.",
            icon = Icons.Default.BarChart,
            iconColor = Color(0xFF0284C7),
            iconBackgroundColor = Color(0xFFF0F9FF),
            onClick = onResultadosClick,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
