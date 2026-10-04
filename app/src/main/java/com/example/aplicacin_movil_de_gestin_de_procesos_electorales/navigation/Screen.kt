package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation

/**
 * ESTRUCTURA BASE DE NAVEGACIÓN
 *
 * Infraestructura inicial de rutas. La definición completa de rutas entre todos los
 * módulos será desarrollada en la siguiente tarea del Sprint.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
}
