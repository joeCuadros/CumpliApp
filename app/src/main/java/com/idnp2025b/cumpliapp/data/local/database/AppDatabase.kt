package com.idnp2025b.cumpliapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.idnp2025b.cumpliapp.data.local.dao.ActividadDao
import com.idnp2025b.cumpliapp.data.local.database.Converters
import com.idnp2025b.cumpliapp.data.local.entity.ActividadEntity

@Database(
    entities = [ActividadEntity::class],
    version = 2, // ACTUALIZADO de 1 a 2
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actividadDao(): ActividadDao
}

// NUEVA MIGRACIÓN: Añadir columnas tiempoAcumulado y enProgreso
val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE actividades ADD COLUMN tiempoAcumulado INTEGER NOT NULL DEFAULT 0")
        db.execSQL("ALTER TABLE actividades ADD COLUMN enProgreso INTEGER NOT NULL DEFAULT 0")
    }
}