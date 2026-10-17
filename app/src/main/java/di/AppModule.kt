package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.di

import android.content.Context
import androidx.room.Room
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CursoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.EstudianteDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.CursoRepositoryImpl
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.EstudianteRepository
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.repository.EstudianteRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // 1. Le enseñamos a Hilt cómo construir la Base de Datos
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "elecciones_db"
        )
            .fallbackToDestructiveMigration() // Ayuda a que no crashee si cambias las tablas mientras desarrollas
            .build()
    }

    // 2. Le enseñamos a Hilt cómo obtener los DAOs
    @Provides
    fun provideCursoDao(database: AppDatabase): CursoDao = database.cursoDao()

    @Provides
    fun provideEstudianteDao(database: AppDatabase): EstudianteDao = database.estudianteDao()

    // 3. Le enseñamos a Hilt cómo conectar tus Interfaces con tus Implementaciones
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
}