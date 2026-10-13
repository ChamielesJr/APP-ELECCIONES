package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.votacion

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HowToVote
import androidx.compose.material.icons.filled.School
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity

private val AzulMarinoPrincipal = Color(0xFF101B55)
private val AzulBotonPrincipal = Color(0xFF2468F2)
private val AzulGrisaceoSecundario = Color(0xFF667DAA)
private val FondoGeneral = Color(0xFFF5F8FE)
private val FondoTarjeta = Color(0xFFFFFFFF)
private val BordeGrisSuave = Color(0xFFE2E8F0)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VotacionScreen(
    onVolverAlLogin: () -> Unit = {},
    viewModel: VotacionViewModel? = null
) {
    val context = LocalContext.current
    val database = AppDatabase.getInstance(context)

    val activeViewModel: VotacionViewModel = viewModel ?: run {
        viewModel(
            factory = VotacionViewModel.Factory(
                database.institucionDao(),
                database.procesoElectoralDao(),
                database.cursoDao(),
                database.estudianteDao(),
                database.listaElectoralDao(),
                database.candidatoDao(),
                database.votacionDao()
            )
        )
    }

    val uiState by activeViewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.mensajeError) {
        uiState.mensajeError?.let {
            snackbarHostState.showSnackbar(it)
            activeViewModel.limpiarMensaje()
        }
    }

    // Modal de Confirmación
    if (uiState.mostrarConfirmacionDialog) {
        val opcionNombre = if (uiState.esVotoNulo) "VOTO NULO" else uiState.opcionSeleccionada?.nombre ?: "Sin Selección"
        AlertDialog(
            onDismissRequest = { activeViewModel.cancelarConfirmacion() },
            title = { Text("CONFIRMAR VOTO", fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal) },
            text = {
                Text(
                    text = "Has seleccionado la opción: «$opcionNombre».\n\n¿Estás seguro de que deseas registrar tu voto?\nUna vez confirmado, no podrás modificarlo.",
                    color = AzulGrisaceoSecundario
                )
            },
            confirmButton = {
                Button(
                    onClick = { activeViewModel.confirmarVotoDefinitivo() },
                    colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal)
                ) {
                    Text("CONFIRMAR VOTO", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { activeViewModel.cancelarConfirmacion() }) {
                    Text("CANCELAR", color = AzulGrisaceoSecundario)
                }
            },
            containerColor = Color.White
        )
    }

    Scaffold(
        containerColor = FondoGeneral,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(containerColor = FondoTarjeta),
                title = {
                    Column(verticalArrangement = Arrangement.Center) {
                        Text("Módulo de Votación Estudiantil", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                        Text("Distrito 13D02 — Elecciones Escolares", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            when (uiState.pasoActual) {
                1 -> Paso1SeleccionarInstitucion(
                    uiState = uiState,
                    onSeleccionarPared = { pair -> activeViewModel.seleccionarInstitucion(pair) },
                    onVolverLogin = onVolverAlLogin
                )
                2 -> Paso2IdentificacionEstudiante(
                    uiState = uiState,
                    onCursoSelected = { c -> activeViewModel.seleccionarCurso(c) },
                    onNumeroChange = { num -> activeViewModel.onNumeroListaChange(num) },
                    onContinuar = { activeViewModel.validarEstudianteYContinuar() },
                    onAtras = { activeViewModel.regresarPasoAnterior() }
                )
                3 -> Paso3OpcionesElectorales(
                    uiState = uiState,
                    onOpcionSelect = { lista, nulo -> activeViewModel.seleccionarOpcionVoto(lista, nulo) },
                    onConfirmarClick = { activeViewModel.solicitarConfirmacion() },
                    onAtras = { activeViewModel.regresarPasoAnterior() }
                )
                4 -> Paso4VotoExitoso(
                    onFinalizar = {
                        activeViewModel.reiniciarFlujo()
                        onVolverAlLogin()
                    }
                )
            }
        }
    }
}

@Composable
private fun Paso1SeleccionarInstitucion(
    uiState: VotacionUiState,
    onSeleccionarPared: (Pair<com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity, com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity>) -> Unit,
    onVolverLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEFF6FF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AzulBotonPrincipal, modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Paso 1: Seleccione su Institución", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                Text("Elija el plantel educativo donde cursa sus estudios.", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
            }
        }

        if (uiState.cargando) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AzulBotonPrincipal)
        } else if (uiState.institucionesConProcesos.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                border = BorderStroke(1.dp, BordeGrisSuave)
            ) {
                Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("No hay procesos electorales habilitados en este momento.", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = AzulMarinoPrincipal, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onVolverLogin, colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal)) {
                        Text("Regresar al Inicio de Sesión")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = uiState.institucionesConProcesos, key = { it.first.id }) { pair ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSeleccionarPared(pair) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
                        border = BorderStroke(1.dp, BordeGrisSuave),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(pair.first.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                                Text("Proceso: ${pair.second.titulo}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = AzulBotonPrincipal)
                            }
                            Icon(Icons.Default.ArrowForward, contentDescription = "Seleccionar", tint = AzulBotonPrincipal)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onVolverLogin,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Regresar al Inicio")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Paso2IdentificacionEstudiante(
    uiState: VotacionUiState,
    onCursoSelected: (com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity) -> Unit,
    onNumeroChange: (String) -> Unit,
    onContinuar: () -> Unit,
    onAtras: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF0FDF4)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.School, contentDescription = null, tint = Color(0xFF16A34A), modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Paso 2: Identificación del Estudiante", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                Text("Institución: ${uiState.institucionSeleccionada?.nombre}", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = FondoTarjeta),
            border = BorderStroke(1.dp, BordeGrisSuave)
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Seleccione su Aula de Clases", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)

                var expCurso by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expCurso,
                    onExpandedChange = { expCurso = !expCurso },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.cursoSeleccionado?.let { "${it.grado} '${it.paralelo}' - ${it.jornada} (${it.nivel})" } ?: "Seleccionar Curso, Paralelo y Jornada",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Curso / Paralelo / Jornada *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expCurso) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expCurso,
                        onDismissRequest = { expCurso = false }
                    ) {
                        uiState.cursosDisponibles.forEach { curso ->
                            DropdownMenuItem(
                                text = { Text("${curso.grado} '${curso.paralelo}' — ${curso.jornada} (${curso.nivel})") },
                                onClick = {
                                    onCursoSelected(curso)
                                    expCurso = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = uiState.numeroListaIngresado,
                    onValueChange = onNumeroChange,
                    label = { Text("Número de Lista del Estudiante (Ej: 1, 2, 15) *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Text(
                    text = "* Nota: Ingrese el puesto o número ordinal que le corresponde en la lista de asistencia de su profesor.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AzulGrisaceoSecundario
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onAtras, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp)) {
                Text("Atrás")
            }
            Button(
                onClick = onContinuar,
                enabled = uiState.cursoSeleccionado != null && uiState.numeroListaIngresado.isNotBlank(),
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal)
            ) {
                Text("Continuar a Votar", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Paso3OpcionesElectorales(
    uiState: VotacionUiState,
    onOpcionSelect: (ListaElectoralEntity?, Boolean) -> Unit,
    onConfirmarClick: () -> Unit,
    onAtras: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFFFBEB)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.HowToVote, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(28.dp))
            }
            Column {
                Text("Paso 3: Seleccione su Opción de Voto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                Text("Estudiante: ${uiState.estudianteValidado?.nombre} ${uiState.estudianteValidado?.apellido} (#${uiState.estudianteValidado?.numeroLista})", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(items = uiState.listasElectorales, key = { it.id }) { lista ->
                val seleccionada = uiState.opcionSeleccionada?.id == lista.id && !uiState.esVotoNulo
                val candidatosLista = uiState.candidatos.filter { it.listaId == lista.id }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpcionSelect(lista, false) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (seleccionada) Color(0xFFEFF6FF) else FondoTarjeta),
                    border = BorderStroke(if (seleccionada) 2.dp else 1.dp, if (seleccionada) AzulBotonPrincipal else BordeGrisSuave)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(try { Color(android.graphics.Color.parseColor(lista.color)) } catch (_: Exception) { AzulGrisaceoSecundario }),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("N° ${lista.numero}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(lista.nombre, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal)
                            if (candidatosLista.isNotEmpty()) {
                                Text("Candidatos: ${candidatosLista.take(2).joinToString { "${it.cargo}: ${it.nombres}" }}", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
                            }
                        }

                        if (seleccionada) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Seleccionado", tint = AzulBotonPrincipal, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }

            item {
                val nuloSeleccionado = uiState.esVotoNulo
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpcionSelect(null, true) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = if (nuloSeleccionado) Color(0xFFFEE2E2) else FondoTarjeta),
                    border = BorderStroke(if (nuloSeleccionado) 2.dp else 1.dp, if (nuloSeleccionado) Color(0xFFDC2626) else BordeGrisSuave)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDC2626)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("X", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text("VOTO NULO", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF991B1B))
                            Text("Elija esta opción si decide no sufragar por ninguna lista.", style = MaterialTheme.typography.bodySmall, color = AzulGrisaceoSecundario)
                        }

                        if (nuloSeleccionado) {
                            Icon(Icons.Default.CheckCircle, contentDescription = "Seleccionado", tint = Color(0xFFDC2626), modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            OutlinedButton(onClick = onAtras, modifier = Modifier.weight(1f).height(48.dp), shape = RoundedCornerShape(12.dp)) {
                Text("Atrás")
            }
            Button(
                onClick = onConfirmarClick,
                enabled = uiState.esVotoNulo || uiState.opcionSeleccionada != null,
                modifier = Modifier.weight(1f).height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal)
            ) {
                Text("Emitir Voto", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun Paso4VotoExitoso(onFinalizar: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFD6FAE7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.CheckCircle, contentDescription = "Exitoso", tint = Color(0xFF16A34A), modifier = Modifier.size(48.dp))
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text("¡VOTO REGISTRADO!", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = AzulMarinoPrincipal, textAlign = TextAlign.Center)

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Tu voto se ha registrado de forma segura y anónima en el sistema.\nGracias por participar en este proceso electoral.",
            style = MaterialTheme.typography.bodyMedium,
            color = AzulGrisaceoSecundario,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = onFinalizar,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AzulBotonPrincipal)
        ) {
            Text("FINALIZAR", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
    }
}
