package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListasScreen(
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
) {
    val context = LocalContext.current
    val factory = remember(context) {
        ListaElectoralViewModelFactory(
            ListaElectoralRepository(
                AppDatabase.getInstance(context).listaElectoralDao()
            )
        )
    }
    val viewModel: ListaElectoralViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var mostrarAviso by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Listas electorales")
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Gestiona las listas de tu institución educativa.",
                style = MaterialTheme.typography.bodyLarge
            )
            Button(
                onClick = { mostrarAviso = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrar nueva lista")
            }
            when {
                uiState.cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
                uiState.mensajeError != null -> {
                    Text(
                        text = "Error al cargar las listas: ${uiState.mensajeError}",
                        color = MaterialTheme.colorScheme.error
                    )
                }
                uiState.listas.isEmpty() -> {
                    Text(
                        text = "Todavía no hay listas electorales registradas.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                else -> {
                    Text(
                        text = "Listas registradas: ${uiState.listas.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(
                            items = uiState.listas,
                            key = { it.id }
                        ) { lista ->
                            ListaElectoralCard(lista = lista)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Regresar al Dashboard")
            }
        }
    }

    if (mostrarAviso) {
        AlertDialog(
            onDismissRequest = { mostrarAviso = false },
            title = {
                Text("Registrar lista electoral")
            },
            text = {
                Text(
                    "para registrar y editar listas electorales."
                )
            },
            confirmButton = {
                Button(
                    onClick = { mostrarAviso = false }
                ) {
                    Text("Entendido")
                }
            }
        )
    }
}

@Composable
private fun ListaElectoralCard(
    lista: ListaElectoralEntity
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = lista.nombre,
                style = MaterialTheme.typography.titleMedium
            )
            Text("Número: ${lista.numero}")
            if (lista.descripcion.isNullOrBlank().not()) {
                Text("Descripción: ${lista.descripcion}")
            }

            Text("Color: ${lista.color}")
            Text(
                text = if (lista.estado) "Estado: Activa"
                else "Estado: Inactiva",
                color = if (lista.estado) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.error
                }
            )
        }
    }
}

private class ListaElectoralViewModelFactory(
    private val repository: ListaElectoralRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {
        if (modelClass.isAssignableFrom(ListaElectoralViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListaElectoralViewModel(repository) as T
        }
        throw IllegalArgumentException(
            "ViewModel desconocido: ${modelClass.name}"
        )
    }
}
