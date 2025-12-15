package com.idnp2025b.cumpliapp.di

import com.idnp2025b.cumpliapp.data.repository.ActividadRepository
import com.idnp2025b.cumpliapp.domain.repository.InterfaceActividadRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindActividadRepository(
        actividadRepositoryImpl: ActividadRepository
    ): InterfaceActividadRepository
}