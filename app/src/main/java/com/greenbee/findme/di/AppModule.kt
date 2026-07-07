package com.greenbee.findme.di

import com.greenbee.findme.BuildConfig
import com.greenbee.findme.data.di.PublicDataServiceKey
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @PublicDataServiceKey
    fun providePublicDataServiceKey(): String = BuildConfig.PUBLIC_DATA_SERVICE_KEY
}
