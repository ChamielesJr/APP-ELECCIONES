package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.estudiantes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation

private val AzulElectoral = Color(0xFF2463EB)
private val FondoElectoral = Color(0xFFF4F7FC)
private val TextoSecundario = Color(0xFF6B7280)
private val ContenedorIcono = Color(0xFFE0E7FF)
private val ColorIcono = Color(0xFF3730A3)

/**
 * Pantalla principal de Estudiantes diseñada con el estilo visual de alta fidelidad (Maquetado).
 * Permite desplazamiento vertical completo (ideal para modo horizontal / landscape).
 * Respeta el uso exclusivo de StateFlow sin variables mutables de Compose prohibidas.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudiantesScreen(
    viewModel: EstudiantesViewModel,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
) {
    val estudiantes by viewModel.estudiantesState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Sistema de Elecciones Escolares",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Distrito 13D02",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Estudiantes.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoElectoral)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera del módulo (Título y descripción con icono)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ContenedorIcono),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Estudiantes",
                        tint = ColorIcono,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = "Estudiantes",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1F2937)
                    )
                    Text(
                        text = "Consulta los estudiantes registrados y su estado de votación.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextoSecundario
                    )
                }
            }

            // Tarjeta de Filtros y Búsqueda
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Barra de búsqueda principal
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.updateSearchQuery(it) },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Buscar estudiantes", color = TextoSecundario) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextoSecundario) },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Filtros Fila 1: Grado, Paralelo, Jornada
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FiltroDropdownSimulado(
                            label = "Grado",
                            value = "Todos",
                            modifier = Modifier.weight(1f)
                        )
                        FiltroDropdownSimulado(
                            label = "Paralelo",
                            value = "Todos",
                            modifier = Modifier.weight(1f)
                        )
                        FiltroDropdownSimulado(
                            label = "Jornada",
                            value = "Todas",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Filtros Fila 2: Número de lista y Estado
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            modifier = Modifier.weight(1f),
                            label = { Text("Número de lista") },
                            placeholder = { Text("Ejemplo: 15") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            enabled = false
                        )
                        FiltroDropdownSimulado(
                            label = "Estado",
                            value = "Todos",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Botones Buscar y Limpiar filtros
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AzulElectoral),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buscar", fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 13.sp)
                        }

                        OutlinedButton(
                            onClick = { viewModel.limpiarFiltros() },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar filtros", fontWeight = FontWeight.Bold, color = Color(0xFF4B5563), maxLines = 1, fontSize = 11.sp)
                        }
                    }
                }
            }

            // Sección: Estudiantes registrados
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = AzulElectoral,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Estudiantes registrados",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1F2937)
                            )
                        }
                        Text(
                            text = "Total: ${estudiantes.size} estudiantes",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextoSecundario,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // Cabecera de la tabla de datos
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ID", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.weight(0.8f))
                        Text("GRADO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.weight(1.5f))
                        Text("PARALELO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.weight(1.2f))
                        Text("JORNADA", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.weight(1.5f))
                        Text("ESTADO", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.weight(1.5f))
                        Text("ACCIONES", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.weight(1.5f))
                    }

                    if (estudiantes.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No hay estudiantes registrados o no se encontraron coincidencias.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario
                            )
                        }
                    } else {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            for (item in estudiantes) {
                                // Filas de estudiantes
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FiltroDropdownSimulado(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextoSecundario
            )
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937)
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = TextoSecundario,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
