package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity

/**
 * Base de datos principal de la aplicación con Room Database.
 * Nombre de la base de datos local SQLite: elecciones_escolares.db
 */
@Database(
    entities = [InstitucionEntity::class,
        ListaElectoralEntity::class,],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun institucionDao(): InstitucionDao
    abstract fun listaElectoralDao(): ListaElectoralDao

    companion object {
        private const val DATABASE_NAME = "elecciones_escolares.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Retorna la instancia Singleton de AppDatabase.
         * Seguro para hilos de ejecución concurrentes (Thread-Safe).
         */
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
