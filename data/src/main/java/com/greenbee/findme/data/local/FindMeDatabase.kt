package com.greenbee.findme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greenbee.findme.data.local.favorite.FavoriteAnimalDao
import com.greenbee.findme.data.local.favorite.FavoriteAnimalEntity
import com.greenbee.findme.data.local.recent.RecentlyViewedDao
import com.greenbee.findme.data.local.recent.RecentlyViewedEntity

@Database(
    entities = [
        FavoriteAnimalEntity::class,
        RecentlyViewedEntity::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class FindMeDatabase : RoomDatabase() {
    abstract fun favoriteAnimalDao(): FavoriteAnimalDao

    abstract fun recentlyViewedDao(): RecentlyViewedDao
}
