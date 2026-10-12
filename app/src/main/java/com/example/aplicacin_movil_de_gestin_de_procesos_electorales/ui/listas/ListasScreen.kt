package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardTopBar
import java.io.File

private val AzulPrimario = Color(0xFF2468F2)
private val AzulMarino = Color(0xFF101B55)
private val AzulGrisaceo = Color(0xFF667DAA)
private val FondoGeneral = Color(0xFFF5F8FE)
private val RojoEliminar = Color(0xFFE52525)
private val VerdeEstadoFondo = Color(0xFFD6FAE7)
private val VerdeEstadoTexto = Color(0xFF176C43)
private val AmarilloCirculo = Color(0xFFFFF0C4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListasScreen(
    currentUserParam: UsuarioEntity? = null,
    onLogoutClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
) {
    val sessionUser by SessionManager.currentUser.collectAsState()
    val currentUser = currentUserParam ?: sessionUser

    var showLogoutDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)

    val factory = remember(context) {
        ListaElectoralViewModelFactory(
            ListaElectoralRepository(
                database.listaElectoralDao(),
                database.candidatoDao(),
                database
            )
        )
    }
    val viewModel: ListaElectoralViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val listasDesactivadas by viewModel.listasDesactivadas.collectAsState()
    val candidatos by database.candidatoDao()
        .obtenerTodos()
        .collectAsState(initial = emptyList())

    var textoBusqueda by rememberSaveable { mutableStateOf("") }
    var mostrarFormulario by rememberSaveable { mutableStateOf(false) }
    var listaEnEdicion by rememberSaveable { mutableStateOf<ListaElectoralEntity?>(null) }
    var listaAEliminar by rememberSaveable { mutableStateOf<ListaElectoralEntity?>(null) }

    if (mostrarFormulario) {
        val candidatosLista = candidatos.filter { it.listaId == (listaEnEdicion?.id ?: 0) }
        val presExistente = candidatosLista.find { it.cargo.equals("Presidente", ignoreCase = true) }
            ?: candidatosLista.firstOrNull()
        val viceExistente = candidatosLista.find { it.cargo.contains("Vice", ignoreCase = true) }
            ?: candidatosLista.getOrNull(1)

        val presNombre = presExistente?.let { "${it.nombres} ${it.apellidos}".trim() } ?: ""
        val viceNombre = viceExistente?.let { "${it.nombres} ${it.apellidos}".trim() } ?: ""

        ListaElectoralFormScreen(
            listaExistente = listaEnEdicion,
            presidenteExistente = presNombre,
            vicepresidenteExistente = viceNombre,
            onGuardarConCandidatos = { entidad, pres, vice ->
                viewModel.guardar(entidad, pres, vice)
                mostrarFormulario = false
                listaEnEdicion = null
            },
            onCancelar = {
                mostrarFormulario = false
                listaEnEdicion = null
            }
        )
        return
    }

    val consulta = textoBusqueda.trim().lowercase()
    val listasFiltradas = uiState.listas.filter { lista ->
        val nombresCandidatos = candidatos
            .filter { it.listaId == lista.id }
            .joinToString(" ") { "${it.nombres} ${it.apellidos}" }
        lista.nombre.lowercase().contains(consulta) ||
                lista.numero.toString().contains(consulta) ||
                (lista.descripcion ?: "").lowercase().contains(consulta) ||
                nombresCandidatos.lowercase().contains(consulta)
    }
    val desactivadasFiltradas = listasDesactivadas.filter { lista ->
        val nombresCandidatos = candidatos
            .filter { it.listaId == lista.id }
            .joinToString(" ") { "${it.nombres} ${it.apellidos}" }
        lista.nombre.lowercase().contains(consulta) ||
                lista.numero.toString().contains(consulta) ||
                (lista.descripcion ?: "").lowercase().contains(consulta) ||
                nombresCandidatos.lowercase().contains(consulta)
    }

    // Diálogo de Confirmación para Eliminar Lista
    if (listaAEliminar != null) {
        AlertDialog(
            onDismissRequest = { listaAEliminar = null },
            title = {
                Text("Eliminar lista electoral", fontWeight = FontWeight.Bold, color = AzulMarino)
            },
            text = {
                Text("¿Está seguro de que desea eliminar la lista «${listaAEliminar?.nombre}»? Esta acción eliminará los registros asociados y no se puede deshacer.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        listaAEliminar?.let { viewModel.eliminar(it) }
                        listaAEliminar = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RojoEliminar)
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { listaAEliminar = null }) {
                    Text("Cancelar", color = AzulMarino)
                }
            }
        )
    }

    // Diálogo de Confirmación para Cerrar Sesión
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = "Cerrar sesión",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF0F172A)
                )
            },
            text = {
                Text(
                    text = "¿Está seguro que desea cerrar la sesión de Administrador?",
                    fontSize = 14.sp,
                    color = Color(0xFF475569)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        SessionManager.logout()
                        onLogoutClick()
                        onNavigate(Screen.Login.route)
                    }
                ) {
                    Text(
                        text = "Cerrar sesión",
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showLogoutDialog = false }
                ) {
                    Text(
                        text = "Cancelar",
                        color = Color(0xFF64748B)
                    )
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = FondoGeneral,
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Listas.route,
                onNavigate = onNavigate
            )
        },
        topBar = {
            DashboardTopBar(
                currentUser = currentUser,
                onLogoutClick = { showLogoutDialog = true }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(AmarilloCirculo),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Assignment,
                        contentDescription = "Listas electorales",
                        tint = AzulPrimario,
                        modifier = Modifier.size(30.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Listas electorales",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino
                    )
                    Text(
                        "Administre las listas participantes en la elección.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AzulGrisaceo
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = AzulMarino
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AzulPrimario)
                ) {
                    Text("← Regresar al dashboard", fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = {
                        listaEnEdicion = null
                        mostrarFormulario = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                ) {
                    Text("+ Registrar lista", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { textoBusqueda = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text(
                                "Buscar por nombre, presidente o vicepresidente...",
                                color = AzulGrisaceo
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Buscar",
                                tint = AzulGrisaceo
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF1F5FD),
                            unfocusedContainerColor = Color(0xFFF1F5FD),
                            focusedBorderColor = AzulPrimario,
                            unfocusedBorderColor = Color.Transparent
                        )
                    )
                    Button(
                        onClick = { /* El filtro se aplica en tiempo real al escribir */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buscar", fontWeight = FontWeight.Bold)
                    }
                }
            }

            when {
                uiState.cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        color = AzulPrimario
                    )
                }
                uiState.mensajeError != null -> {
                    Text(
                        text = uiState.mensajeError ?: "Error al cargar las listas.",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = listasFiltradas,
                            key = { "activa_${it.id}" }
                        ) { lista ->
                            val candidatosLista = candidatos.filter { it.listaId == lista.id }
                            TarjetaListaElectoral(
                                lista = lista,
                                candidatos = candidatosLista,
                                //activa = true,
                                onEditar = {
                                    listaEnEdicion = lista
                                    mostrarFormulario = true
                                },
                                onEliminar = {
                                    listaAEliminar = lista
                                }
                            )
                        }

                        if (desactivadasFiltradas.isNotEmpty()) {
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    "Listas desactivadas (${desactivadasFiltradas.size})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = AzulMarino
                                )
                            }
                            items(
                                items = desactivadasFiltradas,
                                key = { "inactiva_${it.id}" }
                            ) { lista ->
                                val candidatosLista = candidatos.filter { it.listaId == lista.id }
                                TarjetaListaElectoral(
                                    lista = lista,
                                    candidatos = candidatosLista,
                                    //activa = false,
                                    onEditar = {
                                        listaEnEdicion = lista
                                        mostrarFormulario = true
                                    },
                                    onEliminar = {
                                        listaAEliminar = lista
                                    }
                                )
                            }
                        }

                        if (listasFiltradas.isEmpty() && desactivadasFiltradas.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = if (textoBusqueda.isBlank()) {
                                            "No hay listas electorales registradas."
                                        } else {
                                            "No se encontraron listas que coincidan con la búsqueda."
                                        },
                                        modifier = Modifier.padding(20.dp),
                                        color = AzulGrisaceo
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaListaElectoral(
    lista: ListaElectoralEntity,
    candidatos: List<CandidatoEntity>,
    //activa: Boolean,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    val colorLista = remember(lista.color) {
        try {
            Color(android.graphics.Color.parseColor(lista.color))
        } catch (_: Exception) {
            Color.Gray
        }
    }

    val presidente = candidatos.find {
        it.cargo.equals("Presidente", ignoreCase = true) }
        ?: candidatos.firstOrNull()
    val vicepresidente = candidatos.find {
        it.cargo.contains("Vice", ignoreCase = true) }
        ?: candidatos.getOrNull(1)

    /*val presNombre = presidente?.let { "${it.nombres} ${it.apellidos}".trim() }
        ?: if (lista.nombre.trim().equals("Voto Nulo", ignoreCase = true)) "Voto Nulo" else "No registrado"
    val viceNombre = vicepresidente?.let { "${it.nombres} ${it.apellidos}".trim() }
        ?: if (lista.nombre.trim().equals("Voto Nulo", ignoreCase = true)) "No registrado" else "No registrado"*/
    val presNombre = presidente?.let { "${it.nombres} ${it.apellidos}".trim() } ?: "No registrado"
    val viceNombre = vicepresidente?.let { "${it.nombres} ${it.apellidos}".trim() } ?: "No registrado"


    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF1F5FD)),
                    contentAlignment = Alignment.Center
                ) {
                    if (lista.logo.isNotBlank()) {
                        AsyncImage(
                            model = remember(lista.logo) {
                                val archivo = File(lista.logo)

                                if (archivo.isFile) {
                                    archivo
                                } else {
                                    lista.logo
                                }
                            },
                            contentDescription = "Logo ${lista.nombre}",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Person,
                                contentDescription = "Sin logo",
                                tint = AzulGrisaceo,
                                modifier = Modifier.size(32.dp)
                            )
                            Text(
                                "Sin logo",
                                style = MaterialTheme.typography.labelSmall,
                                color = AzulGrisaceo
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = lista.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = AzulMarino
                    )

                    Text(
                        text = "Número de lista: ${lista.numero}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AzulGrisaceo
                    )

                    Text(
                        text = lista.descripcion?.ifBlank { null } ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = AzulGrisaceo
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = AzulGrisaceo,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = presNombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = AzulGrisaceo
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Person,
                            contentDescription = null,
                            tint = AzulGrisaceo,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = viceNombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = AzulGrisaceo
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(colorLista)
                        )
                        Text(
                            text = lista.color.lowercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = AzulGrisaceo
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            //.background(if (activa) VerdeEstadoFondo else Color(0xFFE5E7EB))
                            .background(if (lista.estado) { VerdeEstadoFondo } else { Color(0xFFE5E7EB) })
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            //text = if (activa) "Activa" else "Desactivada",
                            //color = if (activa) VerdeEstadoTexto else Color(0xFF374151),
                            text = if (lista.estado) "Activa" else "Inactiva",
                            color = if (lista.estado) { VerdeEstadoTexto } else { Color(0xFF374151) },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Column(horizontalAlignment = Alignment.End) {
                        Icon(
                            imageVector = Icons.Filled.BarChart,
                            contentDescription = "Votos",
                            tint = AzulMarino,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "0 votos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = AzulMarino
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onEditar,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AzulPrimario)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Editar", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEliminar,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RojoEliminar)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Eliminar",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Eliminar", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private class ListaElectoralViewModelFactory(
    private val repository: ListaElectoralRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListaElectoralViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListaElectoralViewModel(repository) as T
        }
        throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    }
}
