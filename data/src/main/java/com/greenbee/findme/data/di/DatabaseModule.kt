package com.greenbee.findme.data.di

import android.content.Context
import androidx.room.Room
import com.greenbee.findme.data.local.FindMeDatabase
import com.greenbee.findme.data.local.favorite.FavoriteAnimalDao
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
    ).build()

    @Provides
    fun provideFavoriteAnimalDao(database: FindMeDatabase): FavoriteAnimalDao =
        database.favoriteAnimalDao()
}
