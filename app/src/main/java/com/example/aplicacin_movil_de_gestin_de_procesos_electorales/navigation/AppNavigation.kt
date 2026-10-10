package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.candidatos.CandidatosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.ConfiguracionScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.cursos.CursosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.DashboardScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.elecciones.EleccionesScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.estudiantes.EstudiantesScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.instituciones.InstitucionesScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas.ListasScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.login.LoginScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados.ResultadosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.usuarios.UsuariosScreen

/**
 * GRAFO PRINCIPAL DE NAVEGACIÓN
 *
 * Configura las rutas y el flujo de navegación para el módulo Administrador.
 */
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
            )
        }

        composable(Screen.Dashboard.route) {
            ProtectedDestination(navController = navController) {
                DashboardScreen(
                    onInstitucionesClick = { navController.navigate(Screen.Instituciones.route) },
                    onUsuariosClick = { navController.navigate(Screen.Usuarios.route) },
                    onCursosClick = { navController.navigate(Screen.Cursos.route) },
                    onEstudiantesClick = { navController.navigate(Screen.Estudiantes.route) },
                    onEleccionesClick = { navController.navigate(Screen.Elecciones.route) },
                    onListasClick = { navController.navigate(Screen.Listas.route) },
                    onCandidatosClick = { navController.navigate(Screen.Candidatos.route) },
                    onConfiguracionClick = { navController.navigate(Screen.Configuracion.route) },
                    onResultadosClick = { navController.navigate(Screen.Resultados.route) },
                )
            }
        }

        composable(Screen.Instituciones.route) {
            ProtectedDestination(navController = navController) {
                InstitucionesScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Usuarios.route) {
            ProtectedDestination(navController = navController) {
                UsuariosScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Cursos.route) {
            ProtectedDestination(navController = navController) {
                CursosScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Estudiantes.route) {
            ProtectedDestination(navController = navController) {
                EstudiantesScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Elecciones.route) {
            ProtectedDestination(navController = navController) {
                EleccionesScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Listas.route) {
            ProtectedDestination(navController = navController) {
                ListasScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Candidatos.route) {
            ProtectedDestination(navController = navController) {
                CandidatosScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Configuracion.route) {
            ProtectedDestination(navController = navController) {
                ConfiguracionScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }

        composable(Screen.Resultados.route) {
            ProtectedDestination(navController = navController) {
                ResultadosScreen(
                    onBack = { navController.popBackStack() },
                )
            }
        }
    }
}

/**
 * Envoltorio para proteger las rutas administrativas mediante verificación de sesión.
 */
@Composable
private fun ProtectedDestination(
    navController: NavHostController,
    content: @Composable () -> Unit
) {
    val isAuthenticated by SessionManager.isAuthenticated.collectAsState()
    if (!isAuthenticated) {
        LaunchedEffect(Unit) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    } else {
        content()
    }
}
