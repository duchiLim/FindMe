package com.greenbee.findme.data.di

import android.content.Context
import androidx.room.Room
import com.greenbee.findme.data.local.FindMeDatabase
import com.greenbee.findme.data.local.MIGRATION_1_2
import com.greenbee.findme.data.local.MIGRATION_2_3
import com.greenbee.findme.data.local.favorite.FavoriteAnimalDao
import com.greenbee.findme.data.local.favorite.FavoriteShelterDao
import com.greenbee.findme.data.local.recent.RecentlyViewedDao
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
    fun provideFindMeDatabase(
        @ApplicationContext context: Context,
    ): FindMeDatabase = Room.databaseBuilder(
        context,
        FindMeDatabase::class.java,
        "findme.db",
    )
        .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
        .build()

    @Provides
    fun provideFavoriteAnimalDao(database: FindMeDatabase): FavoriteAnimalDao =
        database.favoriteAnimalDao()

    @Provides
    fun provideFavoriteShelterDao(database: FindMeDatabase): FavoriteShelterDao =
        database.favoriteShelterDao()

    @Provides
    fun provideRecentlyViewedDao(database: FindMeDatabase): RecentlyViewedDao =
        database.recentlyViewedDao()
}
