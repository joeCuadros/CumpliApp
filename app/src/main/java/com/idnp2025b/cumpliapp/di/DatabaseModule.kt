package com.idnp2025b.cumpliapp.di

import android.content.Context
import androidx.room.Room
import com.idnp2025b.cumpliapp.data.local.AppDatabase
import com.idnp2025b.cumpliapp.data.local.MIGRATION_1_2
import com.idnp2025b.cumpliapp.data.local.dao.ActividadDao
import com.idnp2025b.cumpliapp.data.repository.ActividadRepository
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "cumpliapp_database"
        )
            .addMigrations(MIGRATION_1_2) // NUEVA MIGRACIÓN
            .build()
    }

    @Provides
    @Singleton
    fun provideActividadDao(database: AppDatabase): ActividadDao {
        return database.actividadDao()
    }

    @Provides
    @Singleton
    fun provideActividadRepository(dao: ActividadDao): InterfaceActividadRepository {
        return ActividadRepository(dao)
    }
}