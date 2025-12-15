package com.idnp2025b.cumpliapp.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.idnp2025b.cumpliapp.data.local.dao.ActividadDao
import com.idnp2025b.cumpliapp.data.local.entity.ActividadEntity

@Database(
    entities = [ActividadEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun actividadDao(): ActividadDao

    companion object {
        const val DATABASE_NAME = "actividades_db"
    }
}