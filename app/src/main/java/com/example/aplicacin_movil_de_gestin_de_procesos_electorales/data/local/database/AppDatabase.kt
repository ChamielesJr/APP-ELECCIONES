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
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.ProcesoElectoralDao
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.dao.VotacionDao

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CandidatoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.InstitucionEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ListaElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.UsuarioEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.CursoEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.EstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ProcesoElectoralEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.ParticipacionEstudianteEntity
import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.entity.VotoRegistradoEntity

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
        EstudianteEntity::class,
        ProcesoElectoralEntity::class,
        ParticipacionEstudianteEntity::class,
        VotoRegistradoEntity::class
    ],
    version = 5,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun institucionDao(): InstitucionDao
    abstract fun listaElectoralDao(): ListaElectoralDao
    abstract fun candidatoDao(): CandidatoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun cursoDao(): CursoDao
    abstract fun estudianteDao(): EstudianteDao
    abstract fun procesoElectoralDao(): ProcesoElectoralDao
    abstract fun votacionDao(): VotacionDao

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

        private fun aplicarCambiosVersion5(db: SupportSQLiteDatabase) {
            // 1. Normalizar la tabla 'candidato' eliminando la columna extra 'listaElectoralId' si existe
            try {
                val cursor = db.query("PRAGMA table_info(`candidato`)")
                var tieneListaElectoralId = false
                while (cursor.moveToNext()) {
                    val nameIndex = cursor.getColumnIndex("name")
                    if (nameIndex != -1 && cursor.getString(nameIndex) == "listaElectoralId") {
                        tieneListaElectoralId = true
                        break
                    }
                }
                cursor.close()

                if (tieneListaElectoralId) {
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS `_new_candidato` (
                            `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                            `nombres` TEXT NOT NULL,
                            `apellidos` TEXT NOT NULL,
                            `cargo` TEXT NOT NULL,
                            `foto` TEXT NOT NULL,
                            `listaId` INTEGER NOT NULL
                        )
                        """.trimIndent()
                    )

                    db.execSQL(
                        """
                        INSERT INTO `_new_candidato` (`id`, `nombres`, `apellidos`, `cargo`, `foto`, `listaId`)
                        SELECT `id`, `nombres`, `apellidos`, `cargo`, `foto`, COALESCE(`listaId`, `listaElectoralId`, 0)
                        FROM `candidato`
                        """.trimIndent()
                    )

                    db.execSQL("DROP TABLE `candidato`")
                    db.execSQL("ALTER TABLE `_new_candidato` RENAME TO `candidato`")
                }
            } catch (_: Exception) {}

            // 2. Agregar columna institucionId a 'cursos' si no existe
            try {
                db.execSQL("ALTER TABLE `cursos` ADD COLUMN `institucionId` INTEGER NOT NULL DEFAULT 1")
            } catch (_: Exception) {}

            // 3. Agregar columna numeroLista a 'estudiantes' si no existe
            try {
                db.execSQL("ALTER TABLE `estudiantes` ADD COLUMN `numeroLista` INTEGER NOT NULL DEFAULT 0")
            } catch (_: Exception) {}

            // 4. Crear tabla 'procesos_electorales'
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `procesos_electorales` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `institucionId` INTEGER NOT NULL,
                    `titulo` TEXT NOT NULL,
                    `mensajeInstitucional` TEXT NOT NULL,
                    `fechaInicio` TEXT NOT NULL,
                    `horaInicio` TEXT NOT NULL,
                    `fechaFin` TEXT NOT NULL,
                    `horaFin` TEXT NOT NULL,
                    `estado` TEXT NOT NULL,
                    `fechaCreacion` TEXT NOT NULL,
                    FOREIGN KEY(`institucionId`) REFERENCES `instituciones`(`id`) ON UPDATE NO ACTION ON DELETE RESTRICT
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_procesos_electorales_institucionId` ON `procesos_electorales` (`institucionId`)")

            // 5. Crear tabla 'participacion_estudiantes'
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `participacion_estudiantes` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `estudianteId` INTEGER NOT NULL,
                    `procesoId` INTEGER NOT NULL,
                    `fechaHora` TEXT NOT NULL,
                    FOREIGN KEY(`estudianteId`) REFERENCES `estudiantes`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                    FOREIGN KEY(`procesoId`) REFERENCES `procesos_electorales`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_participacion_estudiantes_estudianteId_procesoId` ON `participacion_estudiantes` (`estudianteId`, `procesoId`)")
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_participacion_estudiantes_procesoId` ON `participacion_estudiantes` (`procesoId`)")

            // 6. Crear tabla 'votos_registrados'
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `votos_registrados` (
                    `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    `procesoId` INTEGER NOT NULL,
                    `listaElectoralId` INTEGER,
                    `esVotoNulo` INTEGER NOT NULL,
                    `fechaHora` TEXT NOT NULL,
                    FOREIGN KEY(`procesoId`) REFERENCES `procesos_electorales`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                )
                """.trimIndent()
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS `index_votos_registrados_procesoId` ON `votos_registrados` (`procesoId`)")
        }

        val MIGRATION_3_5 = object : Migration(3, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                aplicarCambiosVersion5(db)
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `elecciones`")
                aplicarCambiosVersion5(db)
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
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_5, MIGRATION_4_5)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
