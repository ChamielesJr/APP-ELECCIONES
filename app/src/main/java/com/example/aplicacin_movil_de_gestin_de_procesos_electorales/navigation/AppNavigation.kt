package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.security.SessionManager
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.candidatos.CandidatosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.ConfiguracionScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.cursos.CursosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.cursos.CursosViewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.dashboard.DashboardScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.elecciones.EleccionesScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.estudiantes.EstudiantesScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.estudiantes.EstudiantesViewModel
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion.instituciones.InstitucionesScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas.ListasScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.login.LoginScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados.ResultadosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.theme.Aplicación_Movil_De_Gestión_De_Procesos_electoralesTheme
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.usuarios.UsuariosScreen
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.votacion.VotacionScreen
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * GRAFO PRINCIPAL DE NAVEGACIÓN
 * Configura las rutas y el flujo de navegación para el módulo Administrador y Votación Estudiantil.
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
                onIrAVotarClick = {
                    navController.navigate(Screen.Votacion.route)
                }
            )
        }

        composable(Screen.Votacion.route) {
            VotacionScreen(
                onVolverAlLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
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
                    onLogoutSuccess = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }

        composable(Screen.Instituciones.route) {
            ProtectedDestination(navController = navController) {
                InstitucionesScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }

        composable(Screen.Usuarios.route) {
            ProtectedDestination(navController = navController) {
                UsuariosScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }

        composable(Screen.Cursos.route) {
            ProtectedDestination(navController = navController) {
                val context = LocalContext.current
                val database = AppDatabase.getInstance(context)
                val repository = CursoRepositoryImpl(database.cursoDao())
                val viewModel: CursosViewModel = viewModel(
                    factory = CursosViewModel.Factory(repository)
                )

                CursosScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }

        composable(Screen.Estudiantes.route) {
            ProtectedDestination(navController = navController) {
                val viewModel: EstudiantesViewModel = hiltViewModel()

                EstudiantesScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }

        composable(Screen.Elecciones.route) {
            ProtectedDestination(navController = navController) {
                EleccionesScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }

        composable(Screen.Listas.route) {
            ProtectedDestination(navController = navController) {
                Aplicación_Movil_De_Gestión_De_Procesos_electoralesTheme(
                    darkTheme = false,
                    dynamicColor = false
                ) {
                    ListasScreen(
                        onBack = { navController.popBackStack() },
                        onNavigate = { route -> handleBottomNavigation(navController, route) }
                    )
                }
            }
        }

        composable(Screen.Candidatos.route) {
            ProtectedDestination(navController = navController) {
                Aplicación_Movil_De_Gestión_De_Procesos_electoralesTheme(
                    darkTheme = false,
                    dynamicColor = false
                ) {
                    CandidatosScreen(
                        onBack = { navController.popBackStack() },
                    )
                }
            }
        }

        composable(Screen.Configuracion.route) {
            ProtectedDestination(navController = navController) {
                ConfiguracionScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }

        composable(Screen.Resultados.route) {
            ProtectedDestination(navController = navController) {
                ResultadosScreen(
                    onBack = { navController.popBackStack() },
                    onNavigate = { route -> handleBottomNavigation(navController, route) }
                )
            }
        }
    }
}

private fun handleBottomNavigation(navController: NavHostController, targetRoute: String) {
    if (targetRoute == Screen.Dashboard.route) {
        navController.navigate(Screen.Dashboard.route) {
            popUpTo(Screen.Dashboard.route) { inclusive = true }
        }
    } else {
        navController.navigate(targetRoute) {
            popUpTo(Screen.Dashboard.route) { inclusive = false }
            launchSingleTop = true
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
