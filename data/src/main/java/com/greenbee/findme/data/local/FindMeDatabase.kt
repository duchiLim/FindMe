package com.greenbee.findme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greenbee.findme.data.local.favorite.FavoriteAnimalDao
import com.greenbee.findme.data.local.favorite.FavoriteAnimalEntity
import com.greenbee.findme.data.local.favorite.FavoriteShelterDao
import com.greenbee.findme.data.local.favorite.FavoriteShelterEntity
import com.greenbee.findme.data.local.recent.RecentlyViewedDao
import com.greenbee.findme.data.local.recent.RecentlyViewedEntity

@Database(
    entities = [
        FavoriteAnimalEntity::class,
        RecentlyViewedEntity::class,
        FavoriteShelterEntity::class,
    ],
    version = 3,
    exportSchema = false,
)
abstract class FindMeDatabase : RoomDatabase() {
    abstract fun favoriteAnimalDao(): FavoriteAnimalDao

    abstract fun favoriteShelterDao(): FavoriteShelterDao

    abstract fun recentlyViewedDao(): RecentlyViewedDao
}
