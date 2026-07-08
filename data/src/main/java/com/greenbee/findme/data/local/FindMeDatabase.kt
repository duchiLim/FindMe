package com.greenbee.findme.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.greenbee.findme.data.local.favorite.FavoriteAnimalDao
import com.greenbee.findme.data.local.favorite.FavoriteAnimalEntity

@Database(
    entities = [FavoriteAnimalEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class FindMeDatabase : RoomDatabase() {
    abstract fun favoriteAnimalDao(): FavoriteAnimalDao
}
