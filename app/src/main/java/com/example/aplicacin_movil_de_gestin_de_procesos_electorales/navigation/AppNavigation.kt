package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.login.LoginScreen

/**
 * INFRAESTRUCTURA MÍNIMA DE NAVEGACIÓN
 *
 * Permite la ejecución e inicio de la aplicación.
 * El grafo de navegación completo con todos los módulos se desarrollará en la siguiente tarea.
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
            LoginScreen()
        }
    }
}
