package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.TempEntity

/**
 * Base de datos principal de la aplicación con Room Database.
 * Nombre de la base de datos local SQLite: elecciones_escolares.db
 *
 * GUÍA PARA EL EQUIPO DE DESARROLLO:
 * - Para agregar una nueva entidad, impórtala e inclúyela en el parámetro 'entities = [...]'.
 * - Para exponer un nuevo DAO, agrega una función abstracta en esta clase (ej: abstract fun institucionDao(): InstitucionDao).
 */
@Database(
    entities = [TempEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    // Las funciones abstractas de los DAOs reales se agregarán aquí por cada integrante del equipo.

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
