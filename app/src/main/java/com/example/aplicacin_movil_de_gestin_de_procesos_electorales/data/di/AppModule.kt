package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.di

import android.content.Context
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CursoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.EstudianteDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.AuthRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.AuthRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CandidatoRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CandidatoRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.EstudianteRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.EstudianteRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.InstitucionRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ListaElectoralRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.ProcesoElectoralRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.UsuarioRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.UsuarioRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.VotacionRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.VotacionRepositoryImpl
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

    @Provides
    fun provideUsuarioDao(database: AppDatabase): UsuarioDao = database.usuarioDao()

    // 3. Todos los Repositorios inyectables con sus respectivas implementaciones
    @Provides
    @Singleton
    fun provideAuthRepository(usuarioDao: UsuarioDao): AuthRepository {
        return AuthRepositoryImpl(usuarioDao)
    }

    @Provides
    @Singleton
    fun provideCandidatoRepository(candidatoDao: CandidatoDao): CandidatoRepository {
        return CandidatoRepositoryImpl(candidatoDao)
    }

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
        return InstitucionRepositoryImpl(institucionDao, procesoDao)
    }

    @Provides
    @Singleton
    fun provideProcesoElectoralRepository(
        procesoDao: ProcesoElectoralDao,
        institucionDao: InstitucionDao,
        listaDao: ListaElectoralDao,
        votacionDao: VotacionDao
    ): ProcesoElectoralRepository {
        return ProcesoElectoralRepositoryImpl(procesoDao, institucionDao, listaDao, votacionDao)
    }

    @Provides
    @Singleton
    fun provideListaElectoralRepository(
        listaDao: ListaElectoralDao,
        candidatoDao: CandidatoDao,
        database: AppDatabase
    ): ListaElectoralRepository {
        return ListaElectoralRepositoryImpl(listaDao, candidatoDao, database)
    }

    @Provides
    @Singleton
    fun provideUsuarioRepository(
        usuarioDao: UsuarioDao,
        institucionDao: InstitucionDao
    ): UsuarioRepository {
        return UsuarioRepositoryImpl(usuarioDao, institucionDao)
    }

    @Provides
    @Singleton
    fun provideVotacionRepository(
        institucionDao: InstitucionDao,
        procesoDao: ProcesoElectoralDao,
        cursoDao: CursoDao,
        estudianteDao: EstudianteDao,
        listaDao: ListaElectoralDao,
        candidatoDao: CandidatoDao,
        votacionDao: VotacionDao
    ): VotacionRepository {
        return VotacionRepositoryImpl(
            institucionDao,
            procesoDao,
            cursoDao,
            estudianteDao,
            listaDao,
            candidatoDao,
            votacionDao
        )
    }
}
