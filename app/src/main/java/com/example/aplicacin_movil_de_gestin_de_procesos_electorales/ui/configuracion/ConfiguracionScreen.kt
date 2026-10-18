package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes.ConfiguracionHeader
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes.DatosEleccionSection
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes.DatosInstitucionSection
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.componentes.FechasOpcionesSection
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardTopBar

@Composable
fun ConfiguracionScreen(
    currentUserParam: UsuarioEntity? = null,
    onLogoutClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: ConfiguracionViewModel = hiltViewModel() // Hilt inyecta el ViewModel automáticamente
) {
    val context = LocalContext.current

    val sessionUser by SessionManager.currentUser.collectAsStateWithLifecycle()
    val currentUser = currentUserParam ?: sessionUser

    var showLogoutDialog by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var isErrorInstitucion by remember { mutableStateOf(false) }
    var isErrorMensaje by remember { mutableStateOf(false) }
    var isErrorEleccion by remember { mutableStateOf(false) }
    var isErrorEscudo by remember { mutableStateOf(false) }
    var isErrorFechas by remember { mutableStateOf(false) }

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

    MaterialTheme(
        colorScheme = lightColorScheme(
            background = Color.White,
            surface = Color.White,
            onBackground = Color(0xFF1C1B1F),
            onSurface = Color(0xFF1C1B1F)
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color.White
        ) {
            Scaffold(
                containerColor = Color.White,
                topBar = {
                    DashboardTopBar(
                        currentUser = currentUser,
                        onLogoutClick = { showLogoutDialog = true },
                    )
                },
                bottomBar = {
                    DashboardBottomNavigation(
                        currentRoute = Screen.Configuracion.route,
                        onNavigate = onNavigate
                    )
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {

                    ConfiguracionHeader(
                        estado = if (state.habilitarEleccion) "En curso" else "Finalizado",
                        votacionAbierta = state.habilitarEleccion
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DatosInstitucionSection(
                        nombreInstitucion = state.nombreInstitucion,
                        onNombreInstitucionChange = {
                            viewModel.onNombreInstitucionChanged(it)
                            isErrorInstitucion = it.isBlank()
                        },
                        mensajeInstitucional = state.mensajeInstitucional,
                        onMensajeChange = {
                            viewModel.onMensajeInstitucionalChanged(it)
                            isErrorMensaje = it.isBlank()
                        },
                        isErrorNombre = isErrorInstitucion,
                        isErrorMensaje = isErrorMensaje
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    DatosEleccionSection(
                        nombreEleccion = state.nombreEleccion,
                        onNombreEleccionChange = {
                            viewModel.onNombreEleccionChanged(it)
                            isErrorEleccion = it.isBlank()
                        },
                        escudoUri = state.escudoUri,
                        onEscudoSeleccionado = {
                            viewModel.onEscudoSeleccionado(it)
                            isErrorEscudo = it == null
                        },
                        isErrorNombreEleccion = isErrorEleccion,
                        isErrorEscudo = isErrorEscudo
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    FechasOpcionesSection(
                        fechaInicio = state.fechaInicio,
                        onFechaInicioChange = {
                            viewModel.onFechaInicioChanged(it)
                            isErrorFechas = false
                        },
                        horaInicio = state.horaInicio,
                        onHoraInicioChange = {
                            viewModel.onHoraInicioChanged(it)
                            isErrorFechas = false
                        },
                        fechaFin = state.fechaFin,
                        onFechaFinChange = {
                            viewModel.onFechaFinChanged(it)
                            isErrorFechas = false
                        },
                        horaFin = state.horaFin,
                        onHoraFinChange = {
                            viewModel.onHoraFinChanged(it)
                            isErrorFechas = false
                        },
                        habilitarEleccion = state.habilitarEleccion,
                        onHabilitarEleccionChange = { viewModel.onHabilitarEleccionChanged(it) },
                        publicarResultados = state.publicarResultados,
                        onPublicarResultadosChange = { viewModel.onPublicarResultadosChanged(it) },
                        isErrorFechas = isErrorFechas,
                        onGuardarClick = {
                            isErrorInstitucion = state.nombreInstitucion.isBlank()
                            isErrorMensaje = state.mensajeInstitucional.isBlank()
                            isErrorEleccion = state.nombreEleccion.isBlank()
                            isErrorEscudo = state.escudoUri == null
                            isErrorFechas = state.fechaInicio.isBlank() || state.horaInicio.isBlank() ||
                                    state.fechaFin.isBlank() || state.horaFin.isBlank()

                            viewModel.guardarConfiguracion(
                                onExito = {
                                    Toast.makeText(context, "¡Configuración guardada en Base de Datos!", Toast.LENGTH_LONG).show()
                                },
                                onError = { mensaje ->
                                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show()
                                }
                            )
                        },
                        onCancelarClick = {
                            Toast.makeText(context, "Operación cancelada", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    )
                }
            }
        }
    }
}