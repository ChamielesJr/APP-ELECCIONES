package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.listas

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity

data class ListaConCandidatos(
    val lista: ListaElectoralEntity,
    val presidente: String = "No registrado",
    val vicepresidente: String = "No registrado"
)

data class ListasUiState(
    val listas: List<ListaConCandidatos> = emptyList(),
    val busquedaTexto: String = "",
    val isLoading: Boolean = false
)