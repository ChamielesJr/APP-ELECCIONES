package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation.Screen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardBottomNavigation
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.components.DashboardTopBar
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados.componentes.ResultadosResumenSection
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados.componentes.VotosPorListaSection

@Composable
fun ResultadosScreen(
    currentUserParam: UsuarioEntity? = null,
    onLogoutClick: () -> Unit = {},
    onBack: () -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: ResultadosViewModel = hiltViewModel() // Hilt inyecta el ViewModel automáticamente
) {
    val sessionUser by SessionManager.currentUser.collectAsStateWithLifecycle()
    val currentUser = currentUserParam ?: sessionUser

    var showLogoutDialog by remember { mutableStateOf(false) }

    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
                        currentRoute = Screen.Resultados.route,
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
                    ResultadosResumenSection(
                        totalVotos = state.totalVotos,
                        opcionesElectoralesCount = state.opcionesElectoralesCount,
                        onVolverClick = onBack
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    VotosPorListaSection(
                        resultados = state.resultadosListas
                    )
                }
            }
        }
    }
}