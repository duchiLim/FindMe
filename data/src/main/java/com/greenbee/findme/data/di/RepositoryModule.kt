package com.greenbee.findme.data.di

import com.greenbee.findme.data.repository.AbandonedAnimalRepositoryImpl
import com.greenbee.findme.data.repository.AnimalKindRepositoryImpl
import com.greenbee.findme.data.repository.FavoriteRepositoryImpl
import com.greenbee.findme.data.repository.RecentlyViewedRepositoryImpl
import com.greenbee.findme.data.repository.RegionRepositoryImpl
import com.greenbee.findme.data.repository.ShelterRepositoryImpl
import com.greenbee.findme.domain.repository.AbandonedAnimalRepository
import com.greenbee.findme.domain.repository.AnimalKindRepository
import com.greenbee.findme.domain.repository.FavoriteRepository
import com.greenbee.findme.domain.repository.RecentlyViewedRepository
import com.greenbee.findme.domain.repository.RegionRepository
import com.greenbee.findme.domain.repository.ShelterRepository
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
    abstract fun bindAbandonedAnimalRepository(
        impl: AbandonedAnimalRepositoryImpl,
    ): AbandonedAnimalRepository

    @Binds
    @Singleton
    abstract fun bindRegionRepository(
        impl: RegionRepositoryImpl,
    ): RegionRepository

    @Binds
    @Singleton
    abstract fun bindShelterRepository(
        impl: ShelterRepositoryImpl,
    ): ShelterRepository

    @Binds
    @Singleton
    abstract fun bindAnimalKindRepository(
        impl: AnimalKindRepositoryImpl,
    ): AnimalKindRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        impl: FavoriteRepositoryImpl,
    ): FavoriteRepository

    @Binds
    @Singleton
    abstract fun bindRecentlyViewedRepository(
        impl: RecentlyViewedRepositoryImpl,
    ): RecentlyViewedRepository
}
