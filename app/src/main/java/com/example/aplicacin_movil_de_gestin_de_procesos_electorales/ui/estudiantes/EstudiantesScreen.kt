package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.estudiantes

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation

private val AzulElectoral = Color(0xFF3B82F6)
private val FondoElectoral = Color(0xFFF4F7FC)
private val TextoSecundario = Color(0xFF6B7280)
private val AvatarBg = Color(0xFF6366F1)
private val ContenedorIcono = Color(0xFFE0E7FF)
private val ColorIcono = Color(0xFF3730A3)
private val VerdeFondo = Color(0xFFDCFCE7)
private val VerdeTexto = Color(0xFF166534)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstudiantesScreen(
    viewModel: EstudiantesViewModel,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
) {
    val estudiantes by viewModel.estudiantesState.collectAsStateWithLifecycle()
    var mostrarFormulario by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopBarElectoral(onBack) },
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Estudiantes.route,
                onNavigate = onNavigate
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { mostrarFormulario = true },
                containerColor = AzulElectoral,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar estudiante")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(FondoElectoral)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Cabecera del módulo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier.size(48.dp).clip(CircleShape).background(ContenedorIcono),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Group, contentDescription = null, tint = ColorIcono, modifier = Modifier.size(24.dp))
                }
                Column {
                    Text("Estudiantes", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                    Text("Consulta los estudiantes registrados.", style = MaterialTheme.typography.bodySmall, color = TextoSecundario)
                }
            }

            // Tarjeta de Filtros
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Buscar estudiantes", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1F2937))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FiltroDropdownSimulado("Grado", "Todos", Modifier.weight(1f))
                        FiltroDropdownSimulado("Paralelo", "Todos", Modifier.weight(1f))
                        FiltroDropdownSimulado("Jornada", "Todas", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // 1. Campo de texto con el título por fuera (alineado al diseño)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Número de lista", style = MaterialTheme.typography.labelSmall, color = TextoSecundario, modifier = Modifier.padding(bottom = 2.dp), maxLines = 1)
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                placeholder = { Text("Ej: 15", fontSize = 11.sp) },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                enabled = false,
                                colors = OutlinedTextFieldDefaults.colors(
                                    disabledBorderColor = Color(0xFFE5E7EB),
                                    disabledTextColor = Color(0xFF1F2937),
                                    disabledPlaceholderColor = TextoSecundario
                                )
                            )
                        }

                        // 2. Dropdown de Estado
                        FiltroDropdownSimulado("Estado", "Todos", Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = { }, modifier = Modifier.weight(1f).height(44.dp), colors = ButtonDefaults.buttonColors(containerColor = AzulElectoral), shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buscar", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                        OutlinedButton(
                            onClick = { viewModel.limpiarFiltros() }, modifier = Modifier.weight(1f).height(44.dp), shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp), tint = TextoSecundario)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Limpiar", fontWeight = FontWeight.Medium, color = TextoSecundario, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Sección: Tabla de Estudiantes
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)) {

                    // Cabecera de Tarjeta (Título y Total)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = AzulElectoral, modifier = Modifier.size(18.dp))
                            Text("Estudiantes registrados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1F2937))
                        }
                        Text("Total: ${estudiantes.size} estudiantes", style = MaterialTheme.typography.bodySmall, color = AzulElectoral, fontWeight = FontWeight.Medium)
                    }

                    // AHORA LA TABLA TIENE SCROLL HORIZONTAL
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()) // MAGIA AQUÍ: Scroll lateral para ver todas las columnas
                    ) {
                        // Cabecera de la tabla con las 7 columnas
                        Row(
                            modifier = Modifier
                                .background(Color(0xFFF9FAFB))
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("ID", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(50.dp))
                            Text("GRADO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(90.dp))
                            Text("PARALELO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(80.dp))
                            Text("JORNADA", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(90.dp))
                            Text("NÚMERO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(70.dp))
                            Text("ESTADO", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(90.dp))
                            Text("ACCIONES", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = TextoSecundario, modifier = Modifier.width(90.dp))
                        }

                        // Filas conectadas a la Base de Datos (Room)
                        if (estudiantes.isEmpty()) {
                            Box(modifier = Modifier.width(660.dp).padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No hay estudiantes registrados. Usa el botón '+' para agregar el primero.", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario, textAlign = TextAlign.Center)
                            }
                        } else {
                            estudiantes.forEachIndexed { index, item ->
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(item.estudiante.cedula, fontSize = 12.sp, color = TextoSecundario, modifier = Modifier.width(50.dp))
                                    Text(item.curso?.grado ?: "Sin grado", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF1F2937), modifier = Modifier.width(90.dp))
                                    Text(item.curso?.paralelo ?: "-", fontSize = 12.sp, color = TextoSecundario, modifier = Modifier.width(80.dp))
                                    Text(item.curso?.jornada ?: "-", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1F2937), modifier = Modifier.width(90.dp))
                                    Text("${index + 1}", fontSize = 12.sp, color = TextoSecundario, modifier = Modifier.width(70.dp))

                                    Box(modifier = Modifier.width(90.dp)) {
                                        val (colorFondo, colorTexto, texto) = if (item.estudiante.estado) Triple(VerdeFondo, VerdeTexto, "Ya votó") else Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "Pendiente")
                                        Box(modifier = Modifier.clip(RoundedCornerShape(12.dp)).background(colorFondo).padding(horizontal = 8.dp, vertical = 4.dp)) {
                                            Text(texto, color = colorTexto, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    Text("Bloqueado", fontSize = 12.sp, color = Color(0xFF94A3B8), modifier = Modifier.width(90.dp))
                                }
                                if (index < estudiantes.size - 1) {
                                    HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
                                }
                            }
                        }
                    }

                    // Paginación
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp).padding(top = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Mostrando resultados", fontSize = 10.sp, color = TextoSecundario)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(18.dp))
                            Box(modifier = Modifier.size(22.dp).clip(RoundedCornerShape(4.dp)).background(AzulElectoral), contentAlignment = Alignment.Center) {
                                Text("1", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (mostrarFormulario) {
            EstudianteFormDialog(
                onDismiss = { mostrarFormulario = false },
                onSave = { cedula, nombre, apellido -> mostrarFormulario = false }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBarElectoral(onBack: () -> Unit) {
    TopAppBar(
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Sistema de Elecciones Escolares", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1E3A8A), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text("Distrito 13D02", style = MaterialTheme.typography.bodySmall, color = AzulElectoral)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(28.dp).clip(CircleShape).background(AvatarBg), contentAlignment = Alignment.Center) {
                        Text("A", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Admin", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium, maxLines = 1)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(14.dp))
                }
            }
        },
        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = Color(0xFF1E3A8A)) } },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
    )
}

@Composable
fun FiltroDropdownSimulado(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, color = TextoSecundario, modifier = Modifier.padding(bottom = 2.dp), maxLines = 1)
        Surface(shape = RoundedCornerShape(8.dp), color = Color.White, border = BorderStroke(1.dp, Color(0xFFE5E7EB))) {
            Row(modifier = Modifier.fillMaxWidth().height(50.dp).padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(text = value, fontSize = 12.sp, color = Color(0xFF1F2937), maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.KeyboardArrowDown, contentDescription = null, tint = TextoSecundario, modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
fun EstudianteFormDialog(onDismiss: () -> Unit, onSave: (String, String, String) -> Unit) {
    var cedula by remember { mutableStateOf("") }
    var nombre by remember { mutableStateOf("") }
    var apellido by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Registrar Estudiante", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = cedula, onValueChange = { cedula = it }, label = { Text("Cédula") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombres") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = apellido, onValueChange = { apellido = it }, label = { Text("Apellidos") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = { Button(onClick = { onSave(cedula, nombre, apellido) }, colors = ButtonDefaults.buttonColors(containerColor = AzulElectoral), shape = RoundedCornerShape(8.dp)) { Text("Guardar", fontWeight = FontWeight.Bold) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancelar", color = TextoSecundario, fontWeight = FontWeight.Bold) } },
        containerColor = Color.White, shape = RoundedCornerShape(16.dp)
    )
}