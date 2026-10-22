package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.di

import android.content.Context
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.*
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. Instancia Singleton de la Base de Datos
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    // 2. Todos los DAOs que utiliza la app
    @Provides
    fun provideCursoDao(database: AppDatabase): CursoDao = database.cursoDao()

    @Provides
    fun provideEstudianteDao(database: AppDatabase): EstudianteDao = database.estudianteDao()

    @Provides
    fun provideInstitucionDao(database: AppDatabase): InstitucionDao = database.institucionDao()

    @Provides
    fun provideProcesoElectoralDao(database: AppDatabase): ProcesoElectoralDao = database.procesoElectoralDao()

    @Provides
    fun provideListaElectoralDao(database: AppDatabase): ListaElectoralDao = database.listaElectoralDao()

    @Provides
    fun provideCandidatoDao(database: AppDatabase): CandidatoDao = database.candidatoDao()

    @Provides
    fun provideVotacionDao(database: AppDatabase): VotacionDao = database.votacionDao()

    // 3. Todos los Repositorios inyectables
    @Provides
    @Singleton
    fun provideCursoRepository(dao: CursoDao): CursoRepository {
        return CursoRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideEstudianteRepository(dao: EstudianteDao): EstudianteRepository {
        return EstudianteRepositoryImpl(dao)
    }

    @Provides
    @Singleton
    fun provideInstitucionRepository(
        institucionDao: InstitucionDao,
        procesoDao: ProcesoElectoralDao
    ): InstitucionRepository {
        return InstitucionRepository(institucionDao, procesoDao)
    }

    @Provides
    @Singleton
    fun provideProcesoElectoralRepository(
        procesoDao: ProcesoElectoralDao,
        institucionDao: InstitucionDao,
        listaDao: ListaElectoralDao,
        votacionDao: VotacionDao
    ): ProcesoElectoralRepository {
        return ProcesoElectoralRepository(procesoDao, institucionDao, listaDao, votacionDao)
    }

    @Provides
    @Singleton
    fun provideListaElectoralRepository(
        listaDao: ListaElectoralDao,
        candidatoDao: CandidatoDao,
        database: AppDatabase
    ): ListaElectoralRepository {
        return ListaElectoralRepository(listaDao, candidatoDao, database)
    }
}