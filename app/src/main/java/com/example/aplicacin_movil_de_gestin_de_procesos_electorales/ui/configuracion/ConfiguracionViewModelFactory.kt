package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.configuracion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository

class ConfiguracionViewModelFactory(
    private val institucionRepo: InstitucionRepository,
    private val procesoRepo: ProcesoElectoralRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ConfiguracionViewModel::class.java)) {
            // Se entregan directamente las instancias recibidas desde AppContainer
            return ConfiguracionViewModel(institucionRepo, procesoRepo) as T
        }
        throw IllegalArgumentException("Clase ViewModel desconocida")
    }
}