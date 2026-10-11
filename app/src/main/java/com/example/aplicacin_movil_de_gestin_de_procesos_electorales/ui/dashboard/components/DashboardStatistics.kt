package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.School
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun DashboardStatistics(
    totalCursos: Int,
    totalEstudiantes: Int,
    totalListas: Int,
    totalVotos: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Fila 1: Cursos | Estudiantes habilitados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Cursos",
                value = totalCursos.toString(),
                icon = Icons.Default.School,
                iconColor = Color(0xFF3B82F6),
                iconBackgroundColor = Color(0xFFEFF6FF),
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Estudiantes habilitados",
                value = totalEstudiantes.toString(),
                icon = Icons.Default.Groups,
                iconColor = Color(0xFF10B981),
                iconBackgroundColor = Color(0xFFECFDF5),
                modifier = Modifier.weight(1f)
            )
        }

        // Fila 2: Listas electorales | Votos registrados
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatisticCard(
                title = "Listas electorales",
                value = totalListas.toString(),
                icon = Icons.AutoMirrored.Filled.Assignment,
                iconColor = Color(0xFFF59E0B),
                iconBackgroundColor = Color(0xFFFFFBEB),
                modifier = Modifier.weight(1f)
            )

            StatisticCard(
                title = "Votos registrados",
                value = totalVotos.toString(),
                icon = Icons.Default.HowToVote,
                iconColor = Color(0xFF8B5CF6),
                iconBackgroundColor = Color(0xFFF5F3FF),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
