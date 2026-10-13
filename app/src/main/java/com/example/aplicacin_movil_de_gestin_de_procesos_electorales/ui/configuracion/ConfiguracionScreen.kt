package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation

private val AzulMarinoPrincipal = Color(0xFF101B55)
private val AzulBotonPrincipal = Color(0xFF2468F2)
private val AzulGrisaceoSecundario = Color(0xFF667DAA)
private val FondoGeneral = Color(0xFFF5F8FE)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val BordeGrisSuave = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfiguracionScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: ConfiguracionViewModel? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)

    val activeViewModel: ConfiguracionViewModel = viewModel ?: run {
        viewModel(factory = ConfiguracionViewModel.Factory(database.institucionDao(), database.procesoElectoralDao()))
    }

    val uiState by activeViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeError, uiState.mensajeExito) {
        uiState.mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            activeViewModel.limpiarMensajes()
        }
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            activeViewModel.limpiarMensajes()
        }
    }

    var selectedInstId by remember { mutableStateOf<Int?>(null) }
    var titulo by remember { mutableStateOf("Elecciones Estudiantiles 2026") }
    var mensaje by remember { mutableStateOf("Líderes hoy, mejores ciudadanos mañana") }
    var fechaInicio by remember { mutableStateOf("2026-11-01") }
    var horaInicio by remember { mutableStateOf("08:00") }
    var fechaFin by remember { mutableStateOf("2026-11-01") }
    var horaFin by remember { mutableStateOf("16:00") }

    // Preseleccionar la primera institución activa si existe
    LaunchedEffect(uiState.instituciones) {
        if (selectedInstId == null && uiState.instituciones.isNotEmpty()) {
            selectedInstId = uiState.instituciones.first().id
        }
    }

    val instSeleccionada = uiState.instituciones.find { it.id == selectedInstId }

    Scaffold(
        containerColor = FondoGeneral,
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Configuracion.route,
                onNavigate = onNavigate
            )
        },
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoTarjeta),
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("Sistema de Elecciones Escolares", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                        Text("Distrito 13D02", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F3FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Configuración Electoral",
                        tint = Color(0xFF7C3AED),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Configuración Electoral", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                    Text("Crear y parametrizar nuevos procesos electorales.", style = MaterialTheme.typography.bodyMedium, color = AzulGrisaceoSecundario)
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(containerColor = FondoTarjeta, contentColor = AzulMarinoPrincipal)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Volver al Dashboard", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                border = BorderStroke(1.dp, BordeGrisSuave),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Crear Nuevo Proceso Electoral", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)

                    // Dropdown Institución
                    var expInst by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expInst,
                        onExpandedChange = { expInst = !expInst },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = instSeleccionada?.let { "${it.nombre} (${it.codigoInstitucional})" } ?: "Seleccionar Institución",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Institución Educativa *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expInst) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = expInst,
                            onDismissRequest = { expInst = false }
                        ) {
                            uiState.instituciones.forEach { inst ->
                                DropdownMenuItem(
                                    text = { Text("${inst.nombre} (${inst.codigoInstitucional})") },
                                    onClick = {
                                        selectedInstId = inst.id
                                        expInst = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = titulo,
                        onValueChange = { titulo = it },
                        label = { Text("Título del Proceso Electoral *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = mensaje,
                        onValueChange = { mensaje = it },
                        label = { Text("Mensaje Institucional / Lema") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = fechaInicio,
                            onValueChange = { fechaInicio = it },
                            label = { Text("Fecha Inicio (AAAA-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = horaInicio,
                            onValueChange = { horaInicio = it },
                            label = { Text("Hora Inicio (HH:MM)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = fechaFin,
                            onValueChange = { fechaFin = it },
                            label = { Text("Fecha Fin (AAAA-MM-DD)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = horaFin,
                            onValueChange = { horaFin = it },
                            label = { Text("Hora Fin (HH:MM)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Button(
                        onClick = {
                            val id = selectedInstId
                            if (id != null) {
                                activeViewModel.crearProceso(id, titulo, mensaje, fechaInicio, horaInicio, fechaFin, horaFin)
                            }
                        },
                        enabled = selectedInstId != null,
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal)
                    ) {
                        Icon(Icons.Default.AddCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Crear Proceso (Estado Initial: PROGRAMADO)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
