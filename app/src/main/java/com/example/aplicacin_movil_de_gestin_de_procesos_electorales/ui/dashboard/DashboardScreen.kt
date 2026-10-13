package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.AdministrationSection
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardStatistics
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardTopBar
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.WelcomeCard

@Composable
fun DashboardScreen(
    onInstitucionesClick: () -> Unit = {},
    onUsuariosClick: () -> Unit = {},
    onCursosClick: () -> Unit = {},
    onEstudiantesClick: () -> Unit = {},
    onEleccionesClick: () -> Unit = {},
    onListasClick: () -> Unit = {},
    onCandidatosClick: () -> Unit = {},
    onConfiguracionClick: () -> Unit = {},
    onResultadosClick: () -> Unit = {},
    onLogoutSuccess: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onDismissSnackbar()
        }
    }

    Scaffold(
        topBar = {
            DashboardTopBar(
                currentUser = uiState.currentUser,
                onLogoutClick = { viewModel.onLogoutRequested() }
            )
        },
        bottomBar = {
            DashboardBottomNavigation(
                currentRoute = Screen.Dashboard.route,
                onNavigate = { route ->
                    when (route) {
                        Screen.Dashboard.route -> { /* Ya en Inicio */ }
                        Screen.Cursos.route -> onCursosClick()
                        Screen.Estudiantes.route -> onEstudiantesClick()
                        Screen.Listas.route -> onListasClick()
                        Screen.Resultados.route -> onResultadosClick()
                        Screen.Configuracion.route -> onConfiguracionClick()
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Tarjeta de Bienvenida
            WelcomeCard()

            // 2. Tarjetas Estadísticas informativas
            DashboardStatistics(
                totalCursos = uiState.totalCursos,
                totalEstudiantes = uiState.totalEstudiantesHabilitados,
                totalListas = uiState.totalListasElectorales,
                totalVotos = uiState.votosRegistrados
            )

            // 3. Sección "Administración del sistema" (8 tarjetas interactivas)
            AdministrationSection(
                onCursosClick = onCursosClick,
                onEstudiantesClick = onEstudiantesClick,
                onListasClick = onListasClick,
                onUsuariosClick = onUsuariosClick,
                onInstitucionesClick = onInstitucionesClick,
                onEleccionesClick = onEleccionesClick,
                onConfiguracionClick = onConfiguracionClick,
                onResultadosClick = onResultadosClick,
                totalUsuarios = uiState.totalUsuarios,
                totalInstituciones = uiState.totalInstituciones,
                totalProcesos = uiState.totalProcesos
            )

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

    // Diálogo de Confirmación para Cerrar Sesión
    if (uiState.showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onDismissLogoutDialog() },
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
                    onClick = { viewModel.confirmLogout(onLogoutSuccess) }
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
                    onClick = { viewModel.onDismissLogoutDialog() }
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
}
