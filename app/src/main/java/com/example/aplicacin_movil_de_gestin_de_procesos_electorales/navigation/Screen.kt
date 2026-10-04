package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.navigation

/**
 * RUTAS DE NAVEGACIÓN
 *
 * Centraliza las definiciones de rutas para todos los módulos del Administrador.
 */
sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Dashboard : Screen("dashboard")
    data object Instituciones : Screen("instituciones")
    data object Usuarios : Screen("usuarios")
    data object Cursos : Screen("cursos")
    data object Estudiantes : Screen("estudiantes")
    data object Elecciones : Screen("elecciones")
    data object Listas : Screen("listas")
    data object Candidatos : Screen("candidatos")
    data object Configuracion : Screen("configuracion")
    data object Resultados : Screen("resultados")
}
