package com.sanvalero.cellfix.di

import com.sanvalero.cellfix.data.repository.CellFixRepositoryImpl
import com.sanvalero.cellfix.domain.repository.CellFixRepository
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
    abstract fun bindCellFixRepository(impl: CellFixRepositoryImpl): CellFixRepository
}
