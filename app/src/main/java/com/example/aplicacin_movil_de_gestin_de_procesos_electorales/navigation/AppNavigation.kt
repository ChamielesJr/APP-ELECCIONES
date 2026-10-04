package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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

        composable(Screen.Instituciones.route) {
            InstitucionesScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Usuarios.route) {
            UsuariosScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Cursos.route) {
            CursosScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Estudiantes.route) {
            EstudiantesScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Elecciones.route) {
            EleccionesScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Listas.route) {
            ListasScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Candidatos.route) {
            CandidatosScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Configuracion.route) {
            ConfiguracionScreen(
                onBack = { navController.popBackStack() },
            )
        }

        composable(Screen.Resultados.route) {
            ResultadosScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
