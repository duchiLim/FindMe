package com.greenbee.findme.data.di

import com.greenbee.findme.data.repository.SampleRepositoryImpl
import com.greenbee.findme.domain.repository.SampleRepository
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
    abstract fun bindSampleRepository(
        impl: SampleRepositoryImpl,
    ): SampleRepository
}
