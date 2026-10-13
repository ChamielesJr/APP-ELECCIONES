package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.candidatos

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.repository.CandidatoRepository
import kotlinx.coroutines.flow.Flow
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import java.io.File
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CandidatosScreen(
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getInstance(context) }
    val repository = remember { CandidatoRepository(database.candidatoDao()) }
    val factory = remember { CandidatoViewModelFactory(repository) }
    val viewModel: CandidatoViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val formState by viewModel.formState.collectAsState()

    val selectorFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uriSeleccionada ->

        if (uriSeleccionada != null) {
            val rutaFoto = guardarFotoCandidato(
                context = context,
                uri = uriSeleccionada
            )
            if (rutaFoto != null) {
                viewModel.cambiarFoto(rutaFoto)
            }
        }
    }

    val listasFlow: Flow<List<ListaElectoralEntity>> = remember {
        //database.listaElectoralDao().observarTodas()
        database.listaElectoralDao().observarActivas()
    }
    val listas by listasFlow.collectAsState(initial = emptyList())

    var mostrandoFormulario by remember { mutableStateOf(false) }
    var candidatoEnEdicion by remember {
        mutableStateOf<CandidatoEntity?>(null)
    }
    var candidatoPorEliminar by remember {
        mutableStateOf<CandidatoEntity?>(null)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (mostrandoFormulario) {
                            if (candidatoEnEdicion == null) {
                                "Registrar candidato"
                            } else {
                                "Editar candidato"
                            }
                        } else {
                            "Gestión de candidatos"
                        }
                    )
                }
            )
        }
    ) { paddingValues ->

        if (mostrandoFormulario) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                CandidatoFormScreen(
                    listas = listas,
                    candidatoExistente = candidatoEnEdicion,
                    state = formState,
                    onNombresChange = viewModel::cambiarNombres,
                    onApellidosChange = viewModel::cambiarApellidos,
                    onCargoChange = viewModel::cambiarCargo,
                    onFotoChange = viewModel::cambiarFoto,
                    onAbrirMenuCargo = viewModel::abrirMenuCargo,
                    onCerrarMenuCargo = viewModel::cerrarMenuCargo,
                    onSeleccionarFoto = {
                        selectorFoto.launch(PickVisualMediaRequest(
                            ActivityResultContracts.PickVisualMedia.ImageOnly
                        )
                        )
                    },
                    onAbrirMenu = viewModel::abrirMenuLista,
                    onCerrarMenu = viewModel::cerrarMenuLista,
                    onSeleccionarLista = viewModel::seleccionarLista,
                    onGuardar = {
                        viewModel.guardarFormulario(
                            candidatoExistente = candidatoEnEdicion,
                            onGuardado = {
                                mostrandoFormulario = false
                                candidatoEnEdicion = null
                            }
                        )
                    },
                    onCancelar = {
                        mostrandoFormulario = false
                        candidatoEnEdicion = null
                    }
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = {
                        candidatoEnEdicion = null
                        viewModel.iniciarFormulario()
                        mostrandoFormulario = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrar candidato")
                }

                when {
                    uiState.cargando -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(
                                Alignment.CenterHorizontally
                            )
                        )
                    }
                    uiState.mensajeError != null -> {
                        Text(
                            text = uiState.mensajeError
                                ?: "Ocurrió un error.",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    uiState.candidatos.isEmpty() -> {
                        Text("Todavía no hay candidatos registrados.")
                    }else -> {
                    Text(
                        text = "Candidatos registrados: ${uiState.candidatos.size}",
                        style = MaterialTheme.typography.titleMedium
                    )
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(
                            items = uiState.candidatos,
                            key = { it.id }
                        ) { candidato ->

                            val nombreLista = listas
                                .firstOrNull {
                                    it.id == candidato.listaId
                                }?.nombre ?: "Lista no encontrada"

                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    if (candidato.foto.isNotBlank()) {
                                        AsyncImage(
                                            model = File(candidato.foto),
                                            contentDescription = "Fotografía de ${candidato.nombres}",
                                            modifier = Modifier
                                                .size(120.dp)
                                                .align(Alignment.CenterHorizontally)
                                                .clip(RoundedCornerShape(12.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                    Text(
                                        text = "${candidato.nombres} ${candidato.apellidos}",
                                        style = MaterialTheme.typography.titleMedium
                                    )

                                    Text("Cargo: ${candidato.cargo}")
                                    Text("Lista electoral: $nombreLista")

                                    OutlinedButton(
                                        onClick = {
                                            candidatoEnEdicion = candidato
                                            viewModel.iniciarFormulario(candidato)
                                            mostrandoFormulario = true
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Editar")
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            candidatoPorEliminar = candidato
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Eliminar")
                                    }
                                }
                            }
                        }
                    }
                }
                }

                OutlinedButton(
                    onClick = onBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Volver al Dashboard")
                }
            }
        }
    }

    candidatoPorEliminar?.let { candidato ->
        AlertDialog(
            onDismissRequest = {
                candidatoPorEliminar = null
            },
            title = {
                Text("Eliminar candidato")
            },
            text = {
                Text(
                    "¿Deseas eliminar a ${candidato.nombres} ${candidato.apellidos}?"
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.eliminar(candidato)
                        candidatoPorEliminar = null
                    }
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        candidatoPorEliminar = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun guardarFotoCandidato(
    context: Context,
    uri: Uri
): String? {
    return try {
        val carpetaFotos = File(
            context.filesDir,
            "fotos_candidatos"
        )

        if (!carpetaFotos.exists()) {
            carpetaFotos.mkdirs()
        }

        val archivoDestino = File(
            carpetaFotos,
            "candidato_${System.currentTimeMillis()}.jpg"
        )

        context.contentResolver.openInputStream(uri)?.use { entrada ->
            archivoDestino.outputStream().use { salida ->
                entrada.copyTo(salida)
            }
        } ?: return null

        archivoDestino.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}