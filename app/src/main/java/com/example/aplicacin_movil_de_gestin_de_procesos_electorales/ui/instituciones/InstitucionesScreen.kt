package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.instituciones

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.remember
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
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation

private val AzulMarinoPrincipal = Color(0xFF101B55)
private val AzulBotonPrincipal = Color(0xFF2468F2)
private val AzulGrisaceoSecundario = Color(0xFF667DAA)
private val FondoGeneral = Color(0xFFF5F8FE)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val BordeGrisSuave = Color(0xFFE2E8F0)
private val VerdeEstadoFondo = Color(0xFFD6FAE7)
private val VerdeEstadoTexto = Color(0xFF176C43)
private val RojoEliminar = Color(0xFFE52525)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InstitucionesScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: InstitucionesViewModel? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)

    val activeViewModel: InstitucionesViewModel = viewModel ?: run {
        val repository = InstitucionRepository(database.institucionDao(), database.procesoElectoralDao())
        viewModel(factory = InstitucionesViewModel.Factory(repository))
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

    if (uiState.mostrarDialogoFormulario) {
        InstitucionFormDialog(
            institucionEditando = uiState.institucionEnEdicion,
            onDismiss = { activeViewModel.onCerrarFormulario() },
            onGuardar = { nom, cod, dir, tel, cor, dis, est ->
                activeViewModel.guardarInstitucion(nom, cod, dir, tel, cor, dis, est)
            }
        )
    }

    uiState.institucionADesactivar?.let { inst ->
        AlertDialog(
            onDismissRequest = { activeViewModel.cancelarDesactivacion() },
            title = { Text("Desactivar institución", fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal) },
            text = { Text("¿Está seguro que desea desactivar «${inst.nombre}»? La institución no estará disponible para nuevos procesos electorales.", color = AzulGrisaceoSecundario) },
            confirmButton = {
                Button(
                    onClick = { activeViewModel.confirmarDesactivacion() },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoEliminar)
                ) {
                    Text("Desactivar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeViewModel.cancelarDesactivacion() }) {
                    Text("Cancelar", color = AzulGrisaceoSecundario)
                }
            },
            containerColor = Color.White
        )
    }

    val consulta = uiState.busquedaTexto.trim().lowercase()
    val institucionesFiltradas = uiState.instituciones.filter { i ->
        i.nombre.lowercase().contains(consulta) ||
                i.codigoInstitucional.lowercase().contains(consulta) ||
                i.correo.lowercase().contains(consulta)
    }

    Scaffold(
        containerColor = FondoGeneral,
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Instituciones.route,
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
                        .background(Color(0xFFECFDF5)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = "Gestión de instituciones",
                        tint = Color(0xFF059669),
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Instituciones Educativas", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                    Text("Administrar planteles del Distrito 13D02.", style = MaterialTheme.typography.bodyMedium, color = AzulGrisaceoSecundario)
                }
            }

            // Fila de Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(containerColor = FondoTarjeta, contentColor = AzulMarinoPrincipal)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Volver al Dashboard", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                Button(
                    onClick = { activeViewModel.onAbrirFormularioNuevo() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Agregar Institución", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // Buscador
            OutlinedTextField(
                value = uiState.busquedaTexto,
                onValueChange = { activeViewModel.onBusquedaChange(it) },
                placeholder = { Text("Buscar institución por nombre o código AMIE...") },
                modifier = Modifier.fillMaxWidth(),
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

            if (uiState.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AzulBotonPrincipal)
            } else if (institucionesFiltradas.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                    border = BorderStroke(1.dp, BordeGrisSuave)
                ) {
                    Text(
                        text = if (uiState.instituciones.isEmpty()) "No hay instituciones educativas registradas." else "No se encontraron instituciones con esa búsqueda.",
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
                    items(items = institucionesFiltradas, key = { it.id }) { inst ->
                        TarjetaInstitucion(
                            institucion = inst,
                            onEditar = { activeViewModel.onAbrirFormularioEditar(inst) },
                            onCambiarEstado = { activeViewModel.solicitarCambioEstado(inst) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaInstitucion(
    institucion: InstitucionEntity,
    onEditar: () -> Unit,
    onCambiarEstado: () -> Unit
) {
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
                    Text(institucion.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                    Text("Código AMIE: ${institucion.codigoInstitucional}", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = AzulBotonPrincipal)
                }

                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (institucion.estado) VerdeEstadoFondo else Color(0xFFF1F5F9))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (institucion.estado) "Activa" else "Inactiva",
                        color = if (institucion.estado) VerdeEstadoTexto else Color(0xFF64748B),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (institucion.direccion.isNotBlank()) {
                Text("Dirección: ${institucion.direccion}", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
            }
            if (institucion.correo.isNotBlank()) {
                Text("Correo: ${institucion.correo}", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onEditar,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal, contentColor = Color.White),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", style = MaterialTheme.typography.labelMedium)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onCambiarEstado,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (institucion.estado) RojoEliminar else AzulBotonPrincipal,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.height(36.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp)
                ) {
                    Icon(if (institucion.estado) Icons.Default.Block else Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (institucion.estado) "Desactivar" else "Reactivar", style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
