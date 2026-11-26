package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.usuarios

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
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
fun UsuariosScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: UsuariosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeError, uiState.mensajeExito) {
        uiState.mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
        uiState.mensajeExito?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensajes()
        }
    }

    if (uiState.mostrarDialogoFormulario) {
        UsuarioFormDialog(
            usuarioEditando = uiState.usuarioEnEdicion,
            instituciones = uiState.instituciones,
            onDismiss = { viewModel.onCerrarFormulario() },
            onGuardar = { nom, ape, cor, pas, conf, rol, instId, est ->
                viewModel.guardarUsuario(nom, ape, cor, pas, conf, rol, instId, est)
            }
        )
    }

    // Diálogo de confirmación para desactivación
    uiState.usuarioADesactivar?.let { user ->
        AlertDialog(
            onDismissRequest = { viewModel.cancelarDesactivacion() },
            title = { Text("Desactivar cuenta", fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal) },
            text = { Text("¿Está seguro que desea desactivar la cuenta de ${user.nombre} ${user.apellido}? El usuario no podrá ingresar al sistema.", color = AzulGrisaceoSecundario) },
            confirmButton = {
                Button(
                    onClick = { viewModel.confirmarDesactivacion() },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoEliminar)
                ) {
                    Text("Desactivar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.cancelarDesactivacion() }) {
                    Text("Cancelar", color = AzulGrisaceoSecundario)
                }
            },
            containerColor = Color.White
        )
    }

    val consulta = uiState.busquedaTexto.trim().lowercase()
    val usuariosFiltrados = uiState.usuarios.filter { u ->
        val coincideBusqueda = u.nombre.lowercase().contains(consulta) ||
                u.apellido.lowercase().contains(consulta) ||
                u.correo.lowercase().contains(consulta)

        val coincideRol = if (uiState.rolFiltro == "Todos") true else u.rol.equals(uiState.rolFiltro, ignoreCase = true)

        coincideBusqueda && coincideRol
    }

    Scaffold(
        containerColor = FondoGeneral,
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Usuarios.route,
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
                        imageVector = Icons.Default.ManageAccounts,
                        contentDescription = "Gestión de usuarios",
                        tint = AzulBotonPrincipal,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Gestión de Usuarios", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                    Text("Administrar cuentas de acceso al sistema.", style = MaterialTheme.typography.bodyMedium, color = AzulGrisaceoSecundario)
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
                    onClick = { viewModel.onAbrirFormularioNuevo() },
                    modifier = Modifier.weight(1f).height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal, contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("+ Nuevo Usuario", fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            // Buscador
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = uiState.busquedaTexto,
                    onValueChange = { viewModel.onBusquedaChange(it) },
                    placeholder = { Text("Buscar nombre o correo...") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    trailingIcon = {
                        if (uiState.busquedaTexto.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onBusquedaChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                            }
                        }
                    }
                )
            }

            // Chips de Filtro por Rol
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Todos", "Administrador", "Rector", "Docente").forEach { r ->
                    FilterChip(
                        selected = uiState.rolFiltro == r,
                        onClick = { viewModel.onRolFiltroChange(r) },
                        label = { Text(r) }
                    )
                }
            }

            if (uiState.cargando) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AzulBotonPrincipal)
            } else if (usuariosFiltrados.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                    border = BorderStroke(1.dp, BordeGrisSuave)
                ) {
                    Text(
                        text = if (uiState.usuarios.isEmpty()) "No hay usuarios registrados." else "No se encontraron usuarios con esos criterios.",
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
                    items(items = usuariosFiltrados, key = { it.id }) { u ->
                        val inst = uiState.instituciones.find { i -> i.id == u.institucionId }
                        TarjetaUsuario(
                            usuario = u,
                            institucionNombre = inst?.nombre ?: "Sin institución asignada",
                            onEditar = { viewModel.onAbrirFormularioEditar(u) },
                            onCambiarEstado = { viewModel.solicitarCambioEstado(u) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaUsuario(
    usuario: UsuarioEntity,
    institucionNombre: String,
    onEditar: () -> Unit,
    onCambiarEstado: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
        border = BorderStroke(1.dp, BordeGrisSuave)
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
                    Text(
                        text = "${usuario.nombre} ${usuario.apellido}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulMarinoPrincipal
                    )
                    Text(
                        text = usuario.correo,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulGrisaceoSecundario
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (usuario.estado) VerdeEstadoFondo else Color(0xFFFEE2E2))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (usuario.estado) "ACTIVO" else "INACTIVO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (usuario.estado) VerdeEstadoTexto else RojoEliminar
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Rol: ${usuario.rol}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = AzulMarinoPrincipal)
                    if (usuario.institucionId != null) {
                        Text("Plantel: $institucionNombre", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onEditar) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar usuario", tint = AzulBotonPrincipal)
                    }
                    IconButton(onClick = onCambiarEstado) {
                        Icon(
                            imageVector = if (usuario.estado) Icons.Default.Block else Icons.Default.CheckCircle,
                            contentDescription = if (usuario.estado) "Desactivar" else "Reactivar",
                            tint = if (usuario.estado) RojoEliminar else VerdeEstadoTexto
                        )
                    }
                }
            }
        }
    }
}
