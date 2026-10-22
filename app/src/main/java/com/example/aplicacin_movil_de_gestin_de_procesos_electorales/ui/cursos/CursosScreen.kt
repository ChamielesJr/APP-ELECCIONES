package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.cursos

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation

// Paleta de colores del diseño
val DarkBlue = Color(0xFF1E1B4B)
val PrimaryBlue = Color(0xFF2563EB)
val LightBlueBg = Color(0xFFEFF6FF)
val LightBlueText = Color(0xFF1D4ED8)
val DangerRed = Color(0xFFDC2626)
val TextGray = Color(0xFF6B7280)
val BorderGray = Color(0xFFE5E7EB)
val BackgroundGray = Color(0xFFF4F7F9)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CursosScreen(
    viewModel: CursosViewModel,
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val cursos by viewModel.cursosState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    // Estados para controlar el formulario modal
    var showFormDialog by remember { mutableStateOf(false) }
    var cursoAEditar by remember { mutableStateOf<CursoEntity?>(null) }

    Scaffold(
        containerColor = BackgroundGray,
        topBar = {
            // Cabecera con protección de barra de estado para que no se aplaste arriba
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundGray)
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = DarkBlue)
                }

                // 2. Textos principales alineados
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 2.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Sistema de Elecciones Escolares",
                        fontSize = 10.5.sp, // Tamaño exacto para que entre completo
                        fontWeight = FontWeight.Bold,
                        color = DarkBlue,
                        maxLines = 1,
                        softWrap = false // Forza a que no se rompa la línea
                    )
                    Text(
                        text = "Distrito 13D02",
                        fontSize = 9.sp,
                        color = TextGray,
                        modifier = Modifier.offset(y = (-2).dp) // Sube el distrito para que quede pegadito
                    )
                }

                // 3. Perfil de Administrador
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(DarkBlue, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("A", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Administrador", fontSize = 10.sp, color = DarkBlue, fontWeight = FontWeight.Medium)
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Opciones", tint = DarkBlue, modifier = Modifier.size(16.dp))
                }
            }
        },
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Cursos.route,
                onNavigate = onNavigate
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // 1. Cabecera (Icono, Títulos y Botón)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFE0E7FF), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Cursos",
                            tint = DarkBlue,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(verticalArrangement = Arrangement.spacedBy((-1).dp)) {
                        Text("Cursos", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
                        Text(
                            text = "Administre los cursos, paralelos y jornadas.",
                            fontSize = 10.sp,
                            color = TextGray,
                            maxLines = 2,
                            lineHeight = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        cursoAEditar = null // Indicamos que es un registro nuevo
                        showFormDialog = true // Mostramos el diálogo
                    },
                    modifier = Modifier.height(38.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar curso", fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Buscador (Altura liberada para centrar el texto)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar curso, paralelo o jornada...", color = TextGray, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Buscar", tint = TextGray, modifier = Modifier.size(22.dp)) },
                textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp), // <-- TEXTO NEGRO Y NEGRITA
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.Black, // <-- COLOR AL ESCRIBIR
                    unfocusedTextColor = Color.Black, // <-- COLOR AL SALIR
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedBorderColor = BorderGray,
                    focusedBorderColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Tabla de Datos
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, BorderGray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    // Encabezados
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAFB))
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TablaHeader("CURSO", 110.dp)
                        TablaHeader("PARALELO", 90.dp)
                        TablaHeader("JORNADA", 110.dp)
                        TablaHeader("ESTUDIANTES", 90.dp, alignCenter = true)
                        TablaHeader("VOTARON", 80.dp, alignCenter = true)
                        TablaHeader("ACCIONES", 110.dp, alignCenter = true)
                    }

                    HorizontalDivider(color = BorderGray, thickness = 1.dp)

                    // Lista de cursos
                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(cursos) { curso ->
                            CursoRow(
                                curso = curso,
                                onEdit = {
                                    cursoAEditar = curso // Pasamos el curso a editar
                                    showFormDialog = true // Abrimos el modal
                                },
                                onDelete = { viewModel.eliminarCurso(curso) }
                            )
                            HorizontalDivider(color = BorderGray, thickness = 0.5.dp)
                        }
                        if (cursos.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("No hay cursos registrados", color = TextGray)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Paginación
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Mostrando 1 a ${if (cursos.size > 10) 10 else cursos.size} de ${cursos.size} resultados",
                    fontSize = 11.sp,
                    color = TextGray
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Anterior", tint = TextGray)
                    }
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(PrimaryBlue, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("1", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    IconButton(onClick = { }, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Siguiente", tint = TextGray)
                    }
                }
            }
        }
    }

    // Modal del Formulario (Aparece cuando showFormDialog es true)
    if (showFormDialog) {
        CursoFormDialog(
            curso = cursoAEditar,
            onDismiss = { showFormDialog = false },
            onSave = { grado, paralelo, jornada, nivel ->
                if (cursoAEditar == null) {
                    // MODO CREAR
                    viewModel.agregarCurso(grado, paralelo, jornada, nivel)
                } else {
                    // MODO EDITAR
                    val editado = cursoAEditar!!.copy(
                        grado = grado,
                        paralelo = paralelo,
                        jornada = jornada,
                        nivel = nivel
                    )
                    viewModel.actualizarCurso(editado)
                }
                showFormDialog = false // Cerramos el diálogo después de guardar
            }
        )
    }
}

@Composable
fun TablaHeader(text: String, width: Dp, alignCenter: Boolean = false) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = TextGray,
        modifier = Modifier.width(width),
        textAlign = if (alignCenter) TextAlign.Center else TextAlign.Start
    )
}

@Composable
fun CursoRow(curso: CursoEntity, onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = curso.grado,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = DarkBlue,
            modifier = Modifier.width(110.dp)
        )

        Text(
            text = if (curso.paralelo.isBlank()) "Sin paralelo" else curso.paralelo,
            fontSize = 13.sp,
            color = if (curso.paralelo.isBlank()) TextGray else DarkBlue,
            modifier = Modifier.width(90.dp)
        )

        Box(modifier = Modifier.width(110.dp)) {
            Box(
                modifier = Modifier
                    .background(LightBlueBg, RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = curso.jornada,
                    fontSize = 11.sp,
                    color = LightBlueText,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Text(
            text = "0",
            fontSize = 13.sp,
            color = DarkBlue,
            modifier = Modifier.width(90.dp),
            textAlign = TextAlign.Center
        )

        Text(
            text = "0",
            fontSize = 13.sp,
            color = DarkBlue,
            modifier = Modifier.width(80.dp),
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier.width(110.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(DangerRed),
                contentAlignment = Alignment.Center
            ) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.White, modifier = Modifier.size(14.dp))
                }
            }
        }
    }
}

@Composable
fun CursoFormDialog(
    curso: CursoEntity? = null,
    onDismiss: () -> Unit,
    onSave: (grado: String, paralelo: String, jornada: String, nivel: String) -> Unit
) {
    var grado by remember { mutableStateOf(curso?.grado ?: "") }
    var paralelo by remember { mutableStateOf(curso?.paralelo ?: "") }
    var jornada by remember { mutableStateOf(curso?.jornada ?: "") }
    var nivel by remember { mutableStateOf(curso?.nivel ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = {
            Text(
                text = if (curso == null) "Registrar Nuevo Curso" else "Editar Curso",
                color = DarkBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = grado,
                    onValueChange = { grado = it },
                    label = { Text("Curso / Grado", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = paralelo,
                    onValueChange = { paralelo = it },
                    label = { Text("Paralelo", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = jornada,
                    onValueChange = { jornada = it },
                    label = { Text("Jornada", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = nivel,
                    onValueChange = { nivel = it },
                    label = { Text("Nivel", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = BorderGray
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(grado, paralelo, jornada, nivel) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Guardar", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar", color = TextGray, fontWeight = FontWeight.Bold)
            }
        }
    )
}