package com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CandidatoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.InstitucionDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ListaElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.UsuarioDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.CursoDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.EstudianteDao

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity

/**
 * Base de datos principal de la aplicación con Room Database.
 * Nombre de la base de datos local SQLite: elecciones_escolares.db
 */
@Database(
    entities = [
        InstitucionEntity::class,
        ListaElectoralEntity::class,
        CandidatoEntity::class,
        UsuarioEntity::class,
        CursoEntity::class,
        EstudianteEntity::class
    ],
    version = 4, // OJO AQUÍ: Subimos la versión a 4
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun institucionDao(): InstitucionDao
    abstract fun listaElectoralDao(): ListaElectoralDao
    abstract fun candidatoDao(): CandidatoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun cursoDao(): CursoDao
    abstract fun estudianteDao(): EstudianteDao

    companion object {
        private const val DATABASE_NAME = "elecciones_escolares.db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `usuarios` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `apellido` TEXT NOT NULL,
                        `correo` TEXT NOT NULL,
                        `contrasena` TEXT NOT NULL,
                        `rol` TEXT NOT NULL,
                        `institucionId` INTEGER,
                        `estado` INTEGER NOT NULL,
                        `fechaRegistro` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `cursos` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `grado` TEXT NOT NULL,
                        `paralelo` TEXT NOT NULL,
                        `jornada` TEXT NOT NULL,
                        `nivel` TEXT NOT NULL,
                        `estado` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `estudiantes` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `cedula` TEXT NOT NULL,
                        `nombre` TEXT NOT NULL,
                        `apellido` TEXT NOT NULL,
                        `cursoId` INTEGER NOT NULL,
                        `voto` INTEGER NOT NULL,
                        `estado` INTEGER NOT NULL,
                        FOREIGN KEY(`cursoId`) REFERENCES `cursos`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )

                db.execSQL("CREATE INDEX IF NOT EXISTS `index_estudiantes_cursoId` ON `estudiantes` (`cursoId`)")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME,
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}