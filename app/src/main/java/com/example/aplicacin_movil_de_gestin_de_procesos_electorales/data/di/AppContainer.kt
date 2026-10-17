package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.di

import android.content.Context
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.*

class AppContainer(private val context: Context) {

    // Instancia única de la base de datos de Room
    val db: AppDatabase by lazy { AppDatabase.getInstance(context) }

    // Repositorio de Instituciones
    val institucionRepository: InstitucionRepository by lazy {
        InstitucionRepository(
            db.institucionDao(),
            db.procesoElectoralDao()
        )
    }

    // Repositorio de Procesos Electorales
    val procesoElectoralRepository: ProcesoElectoralRepository by lazy {
        ProcesoElectoralRepository(
            db.procesoElectoralDao(),
            db.institucionDao(),
            db.listaElectoralDao(),
            db.votacionDao()
        )
    }

    // Repositorio de Listas Electorales (Agregado para Resultados)
    val listaElectoralRepository: ListaElectoralRepository by lazy {
        ListaElectoralRepository(
            listaElectoralDao = db.listaElectoralDao(),
            candidatoDao = db.candidatoDao(),
            database = db
        )
    }
}