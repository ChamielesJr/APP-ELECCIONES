package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.elecciones

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.StopCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
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
fun EleccionesScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: EleccionesViewModel? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)

    val activeViewModel: EleccionesViewModel = viewModel ?: run {
        val repo = ProcesoElectoralRepository(
            database.procesoElectoralDao(),
            database.institucionDao(),
            database.listaElectoralDao(),
            database.votacionDao()
        )
        viewModel(factory = EleccionesViewModel.Factory(repo))
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

    // Diálogo de Confirmación para Transiciones de Estado
    val proc = uiState.procesoAccion
    val accion = uiState.tipoAccion
    if (proc != null && accion != null) {
        val (tituloDlg, msgDlg, colorBtn) = when (accion) {
            "HABILITAR" -> Triple("Habilitar Votación", "¿Desea HABILITAR el proceso «${proc.titulo}»? Los estudiantes autorizados podrán emitir su voto.", Color(0xFF16A34A))
            "SUSPENDER" -> Triple("Suspender Votación", "ADVERTENCIA: ¿Desea SUSPENDER el proceso «${proc.titulo}»? Se pausará temporalmente la emisión de votos.", Color(0xFFEA580C))
            "REANUDAR" -> Triple("Reanudar Votación", "¿Desea REANUDAR el proceso «${proc.titulo}»? Se reactivará la votación estudiantil.", Color(0xFF2563EB))
            "FINALIZAR" -> Triple("Finalizar Proceso Definitivamente", "¡ATENCIÓN! La finalización del proceso «${proc.titulo}» es DEFINITIVA. No se podrán recibir más votos ni reanudar la elección.", Color(0xFFDC2626))
            else -> Triple("Confirmar Acción", "¿Desea continuar?", AzulBotonPrincipal)
        }

        AlertDialog(
            onDismissRequest = { activeViewModel.cancelarAccion() },
            title = { Text(tituloDlg, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal) },
            text = { Text(msgDlg, color = AzulGrisaceoSecundario) },
            confirmButton = {
                Button(
                    onClick = { activeViewModel.confirmarAccion() },
                    colors = ButtonDefaults.buttonColors(containerColor = colorBtn)
                ) {
                    Text("Confirmar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeViewModel.cancelarAccion() }) {
                    Text("Cancelar", color = AzulGrisaceoSecundario)
                }
            },
            containerColor = Color.White
        )
    }

    val consulta = uiState.busquedaTexto.trim().lowercase()
    val procesosFiltrados = uiState.procesos.filter { p ->
        val inst = uiState.instituciones.find { it.id == p.institucionId }
        val instNombre = inst?.nombre?.lowercase() ?: ""
        val coincideBusqueda = p.titulo.lowercase().contains(consulta) || instNombre.contains(consulta)
        val coincideEstado = if (uiState.filtroEstado == "Todos") true else p.estado == uiState.filtroEstado
        coincideBusqueda && coincideEstado
    }

    val totalProg = uiState.procesos.count { it.estado == "PROGRAMADO" }
    val totalHab = uiState.procesos.count { it.estado == "HABILITADO" }
    val totalSusp = uiState.procesos.count { it.estado == "SUSPENDIDO" }
    val totalFin = uiState.procesos.count { it.estado == "FINALIZADO" }

    Scaffold(
        containerColor = FondoGeneral,
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Elecciones.route,
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
                        .background(Color(0xFFEFF6FF)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HowToVote,
                        contentDescription = "Procesos electorales",
                        tint = AzulBotonPrincipal,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Procesos Electorales", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                    Text("Supervisar y controlar elecciones institucionales.", style = MaterialTheme.typography.bodyMedium, color = AzulGrisaceoSecundario)
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

            // Resumen de Estados
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoMiniBadge("Prog.", totalProg, Color(0xFF2563EB), Modifier.weight(1f))
                EstadoMiniBadge("Hab.", totalHab, Color(0xFF16A34A), Modifier.weight(1f))
                EstadoMiniBadge("Susp.", totalSusp, Color(0xFFEA580C), Modifier.weight(1f))
                EstadoMiniBadge("Fin.", totalFin, Color(0xFFDC2626), Modifier.weight(1f))
            }

            // Buscador y Filtro Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = uiState.busquedaTexto,
                    onValueChange = { activeViewModel.onBusquedaChange(it) },
                    placeholder = { Text("Buscar por proceso o institución...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (uiState.busquedaTexto.isNotEmpty()) {
                            IconButton(onClick = { activeViewModel.onBusquedaChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    }
                )

                var expFiltro by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expFiltro,
                    onExpandedChange = { expFiltro = !expFiltro },
                    modifier = Modifier.width(140.dp)
                ) {
                    OutlinedTextField(
                        value = uiState.filtroEstado,
                        onValueChange = {},
                        readOnly = true,
                        singleLine = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expFiltro) },
                        modifier = Modifier.menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expFiltro,
                        onDismissRequest = { expFiltro = false }
                    ) {
                        listOf("Todos", "PROGRAMADO", "HABILITADO", "SUSPENDIDO", "FINALIZADO").forEach { est ->
                            DropdownMenuItem(
                                text = { Text(est) },
                                onClick = {
                                    activeViewModel.onFiltroEstadoChange(est)
                                    expFiltro = false
                                }
                            )
                        }
                    }
                }
            }

            if (uiState.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AzulBotonPrincipal)
            } else if (procesosFiltrados.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                    border = BorderStroke(1.dp, BordeGrisSuave)
                ) {
                    Text(
                        text = if (uiState.procesos.isEmpty()) "No hay procesos electorales creados aún. Utilice 'Configuración' para crear uno." else "No se encontraron procesos electorales para el filtro aplicado.",
                        modifier = Modifier.padding(20.dp),
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulGrisaceoSecundario
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(items = procesosFiltrados, key = { it.id }) { p ->
                        val inst = uiState.instituciones.find { it.id == p.institucionId }
                        TarjetaProcesoElectoral(
                            proceso = p,
                            institucionNombre = inst?.nombre ?: "Institución desconocida",
                            onAccion = { acc -> activeViewModel.solicitarAccion(p, acc) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoMiniBadge(lbl: String, cant: Int, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(cant.toString(), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = color)
            Text(lbl, style = MaterialTheme.typography.labelSmall, color = color)
        }
    }
}

@Composable
private fun TarjetaProcesoElectoral(
    proceso: ProcesoElectoralEntity,
    institucionNombre: String,
    onAccion: (String) -> Unit
) {
    val (colorBg, colorTxt) = when (proceso.estado) {
        "HABILITADO" -> Pair(Color(0xFFD6FAE7), Color(0xFF176C43))
        "SUSPENDIDO" -> Pair(Color(0xFFFFEDD5), Color(0xFFC2410C))
        "FINALIZADO" -> Pair(Color(0xFFFEE2E2), Color(0xFF991B1B))
        else -> Pair(Color(0xFFE0F2FE), Color(0xFF0369A1)) // PROGRAMADO
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(1.dp, BordeGrisSuave),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(proceso.titulo, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                    Text(institucionNombre, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = AzulBotonPrincipal)
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(colorBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = proceso.estado,
                        color = colorTxt,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Text(
                text = "Inicio: ${proceso.fechaInicio} ${proceso.horaInicio} | Fin: ${proceso.fechaFin} ${proceso.horaFin}",
                style = MaterialTheme.typography.bodySmall,
                color = AzulGrisaceoSecundario
            )

            if (proceso.mensajeInstitucional.isNotBlank()) {
                Text(
                    text = "“${proceso.mensajeInstitucional}”",
                    style = MaterialTheme.typography.bodySmall,
                    color = AzulGrisaceoSecundario
                )
            }

            // Acciones por Estado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (proceso.estado) {
                    "PROGRAMADO" -> {
                        Button(
                            onClick = { onAccion("HABILITAR") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Habilitar Votación", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    "HABILITADO" -> {
                        Button(
                            onClick = { onAccion("SUSPENDER") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA580C)),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.PauseCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Suspender", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onAccion("FINALIZAR") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Finalizar", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    "SUSPENDIDO" -> {
                        Button(
                            onClick = { onAccion("REANUDAR") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reanudar Votación", style = MaterialTheme.typography.labelMedium)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = { onAccion("FINALIZAR") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                            modifier = Modifier.height(36.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp)
                        ) {
                            Icon(Icons.Default.StopCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Finalizar", style = MaterialTheme.typography.labelMedium)
                        }
                    }
                    "FINALIZADO" -> {
                        Text(
                            text = "✓ Proceso Concluido Definitivamente",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                    }
                }
            }
        }
    }
}
