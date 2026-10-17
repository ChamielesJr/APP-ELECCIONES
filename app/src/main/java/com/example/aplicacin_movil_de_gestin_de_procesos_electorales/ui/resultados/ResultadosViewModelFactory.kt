package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.ui.resultados

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository

class ResultadosViewModelFactory(
    private val listaElectoralRepository: ListaElectoralRepository,
    private val procesoElectoralRepository: ProcesoElectoralRepository,
    private val votacionDao: VotacionDao,
    private val candidatoDao: CandidatoDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultadosViewModel::class.java)) {
            return ResultadosViewModel(
                listaElectoralRepository = listaElectoralRepository,
                procesoElectoralRepository = procesoElectoralRepository,
                votacionDao = votacionDao,
                candidatoDao = candidatoDao
            ) as T
        }
        throw IllegalArgumentException("Clase ViewModel no reconocida")
    }
}