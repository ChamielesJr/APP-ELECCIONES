package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector,
)

@Composable
fun DashboardBottomNavigation(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val items = listOf(
        BottomNavItem("Inicio", Screen.Dashboard.route, Icons.Default.Home),
        BottomNavItem("Cursos", Screen.Cursos.route, Icons.Default.School),
        BottomNavItem("Estudiantes", Screen.Estudiantes.route, Icons.Default.Groups),
        BottomNavItem("Listas", Screen.Listas.route, Icons.AutoMirrored.Filled.Assignment),
        BottomNavItem("Resultados", Screen.Resultados.route, Icons.Default.BarChart),
        BottomNavItem("Configuración", Screen.Configuracion.route, Icons.Default.Settings),
    )

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        // Se aplica windowInsetsPadding(WindowInsets.navigationBars) en el contenedor interno
        // para que la barra blanca cubra hasta el borde físico del teléfono y los iconos/textos
        // se eleven dinámicamente por encima de los 3 botones de navegación o la barra de gestos.
        Column(
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            HorizontalDivider(color = Color(0xFFE2E8F0), thickness = 1.dp)

            // LazyRow con desplazamiento horizontal para las 6 opciones
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(28.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(items) { item ->
                    val isSelected = currentRoute == item.route
                    val activeColor = Color(0xFF2563EB) // Azul institucional
                    val inactiveColor = Color(0xFF64748B) // Gris azulado

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(MaterialTheme.shapes.small)
                            .clickable {
                                if (!isSelected) {
                                    onNavigate(item.route)
                                }
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = if (isSelected) activeColor else inactiveColor,
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                color = if (isSelected) activeColor else inactiveColor
                            )
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(CircleShape)
                                    .background(activeColor)
                            )
                        }
                    }
                }
            }
        }
    }
}
