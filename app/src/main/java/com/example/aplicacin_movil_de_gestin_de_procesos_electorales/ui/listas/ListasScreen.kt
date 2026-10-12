
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Edit
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation

private val AzulElectoral = Color(0xFF2463EB)
private val FondoElectoral = Color(0xFFF4F7FC)
private val RojoAccion = Color(0xFFDC2626)
private val VerdeEstado = Color(0xFFD1FAE5)
private val TextoEstado = Color(0xFF065F46)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListasScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)

    val factory = androidx.compose.runtime.remember(context) {
        ListaElectoralViewModelFactory(
            ListaElectoralRepository(
                database.listaElectoralDao(),
                database.candidatoDao()
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

    if (mostrarFormulario) {
        ListaElectoralFormScreen(
            listaExistente = listaEnEdicion,
            onGuardar = { lista ->
                viewModel.guardar(lista)
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

    Scaffold(
        containerColor = FondoElectoral,
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Listas.route,
                onNavigate = onNavigate
            )
        },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Sistema de Elecciones Escolares",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Distrito 13D02",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
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
                Column(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFEAB0)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    //Text("☷", color = AzulElectoral, style = MaterialTheme.typography.headlineMedium)
                    Icon(
                        imageVector = Icons.Filled.Assignment,
                        contentDescription = "Listas electorales",
                        tint = AzulElectoral,
                        modifier = Modifier.size(34.dp)
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Listas electorales",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF101B50)
                    )
                    Text(
                        "Administre las listas participantes en la elección.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF65779B)
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("← Regresar al Dashboard")
                }

                Button(
                    onClick = {
                        listaEnEdicion = null
                        mostrarFormulario = true
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("+ Registrar lista")
                }
            }
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = textoBusqueda,
                        onValueChange = { textoBusqueda = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = {
                            Text("Buscar por nombre, número o candidato...")
                        },
                        leadingIcon = {
                            Text("⌕", style = MaterialTheme.typography.headlineSmall)
                        },
                        trailingIcon = {
                            if (textoBusqueda.isNotEmpty()) {
                                TextButton(onClick = { textoBusqueda = "" }) {
                                    Text("Limpiar")
                                }
                            }
                        }
                    )
                    Button(
                        onClick = { /* El filtro se aplica automáticamente al escribir. */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("⌕   Buscar")
                    }
                }
            }
            when {
                uiState.cargando -> {
                    androidx.compose.material3.CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
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
                        item {
                            Text(
                                "Listas activas (${listasFiltradas.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF101B50)
                            )
                        }
                        if (listasFiltradas.isEmpty()) {
                            item {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    )
                                ) {
                                    Text(
                                        text = if (uiState.listas.isEmpty()) {
                                            "Todavía no hay listas registradas. Pulsa «Registrar lista» para comenzar."
                                        } else {
                                            "No se encontraron listas con esa búsqueda."
                                        },
                                        modifier = Modifier.padding(20.dp),
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        items(
                            items = listasFiltradas,
                            key = { "activa_${it.id}" }
                        ) { lista ->
                            val candidatosLista = candidatos.filter {
                                it.listaId == lista.id
                            }
                            TarjetaListaElectoral(
                                lista = lista,
                                nombresCandidatos = candidatosLista.map {
                                    "${it.nombres} ${it.apellidos}"
                                },
                                activa = true,
                                onEditar = {
                                    listaEnEdicion = lista
                                    mostrarFormulario = true
                                },
                                onCambiarEstado = {
                                    viewModel.cambiarEstado(lista)
                                }
                            )
                        }
                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Listas desactivadas (${desactivadasFiltradas.size})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF101B50)
                            )
                        }
                        if (desactivadasFiltradas.isEmpty()) {
                            item {
                                Text(
                                    "No hay listas desactivadas.",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    color = Color.Gray
                                )
                            }
                        }
                        items(
                            items = desactivadasFiltradas,
                            key = { "inactiva_${it.id}" }
                        ) { lista ->
                            val candidatosLista = candidatos.filter {
                                it.listaId == lista.id
                            }
                            TarjetaListaElectoral(
                                lista = lista,
                                nombresCandidatos = candidatosLista.map {
                                    "${it.nombres} ${it.apellidos}"
                                },
                                activa = false,
                                onEditar = {
                                    listaEnEdicion = lista
                                    mostrarFormulario = true
                                },
                                onCambiarEstado = {
                                    viewModel.cambiarEstado(lista)
                                }
                            )
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
    nombresCandidatos: List<String>,
    activa: Boolean,
    onEditar: () -> Unit,
    onCambiarEstado: () -> Unit
) {
    val colorLista = try {
        Color(android.graphics.Color.parseColor(lista.color))
    } catch (_: Exception) {
        Color.Gray
    }

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
                // Zona 1: icono o logo y número de lista.
                Column(
                    modifier = Modifier.width(76.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF0F2F7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assignment,
                            contentDescription = "Logo de la lista",
                            tint = AzulElectoral,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Text(
                        text = "LISTA ${lista.numero}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Zona 2: nombre, descripción y representantes.
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Text(
                        text = lista.nombre,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF101B50)
                    )

                    if (!lista.descripcion.isNullOrBlank()) {
                        Text(
                            text = lista.descripcion,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF65779B)
                        )
                    }

                    if (nombresCandidatos.isEmpty()) {
                        Text(
                            "Sin candidatos registrados",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    } else {
                        nombresCandidatos.take(2).forEachIndexed { index, nombre ->
                            Row(
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(5.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Person,
                                    contentDescription = null,
                                    tint = Color(0xFF65779B),
                                    modifier = Modifier.size(17.dp)
                                )

                                Text(
                                    text = "${if (index == 0) "Presidente" else "Vicepresidente"}: $nombre",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF65779B)
                                )
                            }
                        }
                    }
                }

                // Zona 3: color, estado y espacio reservado para resultados.
                Column(
                    modifier = Modifier.width(88.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Spacer(
                            modifier = Modifier
                                .size(12.dp)
                                .background(colorLista, CircleShape)
                        )

                        Text(
                            text = lista.color,
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF65779B)
                        )
                    }

                    Text(
                        text = if (activa) "Activa" else "Desactivada",
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(
                                if (activa) Color(0xFFD1FAE5)
                                else Color(0xFFE5E7EB)
                            )
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        color = if (activa) Color(0xFF065F46) else Color.DarkGray,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Icon(
                        imageVector = Icons.Filled.BarChart,
                        contentDescription = "Resultados electorales pendientes",
                        tint = Color(0xFF65779B),
                        modifier = Modifier.size(23.dp)
                    )

                    Text(
                        text = "Pendiente",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            // Acciones en la parte inferior derecha.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onEditar,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Editar")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onCambiarEstado,
                    shape = RoundedCornerShape(8.dp),
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = if (activa) RojoAccion else AzulElectoral
                    )
                ) {
                    Icon(
                        imageVector = if (activa) Icons.Filled.Block else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(17.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(if (activa) "Desactivar" else "Reactivar")
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
        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
