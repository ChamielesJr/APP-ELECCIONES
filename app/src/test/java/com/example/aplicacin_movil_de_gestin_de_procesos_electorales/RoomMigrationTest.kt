package com.example.aplicacin_movil_de_gestin_de_procesos_electorales

import com.example.aplicacin_movil_de_gestin_de_procesos_electorales.data.local.database.AppDatabase
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class RoomMigrationTest {

    @Test
    fun migration_3_to_5_isDefined() {
        val migration = AppDatabase.MIGRATION_3_5
        assertNotNull(migration)
        assertEquals(3, migration.startVersion)
        assertEquals(5, migration.endVersion)
    }

    @Test
    fun migration_4_to_5_isDefined() {
        val migration = AppDatabase.MIGRATION_4_5
        assertNotNull(migration)
        assertEquals(4, migration.startVersion)
        assertEquals(5, migration.endVersion)
    }

    @Test
    fun previous_migrations_areDefined() {
        assertEquals(1, AppDatabase.MIGRATION_1_2.startVersion)
        assertEquals(2, AppDatabase.MIGRATION_1_2.endVersion)

        assertEquals(2, AppDatabase.MIGRATION_2_3.startVersion)
        assertEquals(3, AppDatabase.MIGRATION_2_3.endVersion)
    }
}
