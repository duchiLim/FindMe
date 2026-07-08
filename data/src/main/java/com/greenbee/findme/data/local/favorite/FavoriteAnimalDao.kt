package com.greenbee.findme.data.local.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteAnimalDao {

    @Query("SELECT * FROM favorite_animals ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<FavoriteAnimalEntity>>

    @Query("SELECT * FROM favorite_animals WHERE desertionNo = :desertionNo LIMIT 1")
    suspend fun getByDesertionNo(desertionNo: String): FavoriteAnimalEntity?

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_animals WHERE desertionNo = :desertionNo)")
    suspend fun isFavorite(desertionNo: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_animals WHERE desertionNo = :desertionNo)")
    fun observeIsFavorite(desertionNo: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: FavoriteAnimalEntity)

    @Update
    suspend fun update(entity: FavoriteAnimalEntity)

    @Query("DELETE FROM favorite_animals WHERE desertionNo = :desertionNo")
    suspend fun delete(desertionNo: String)
}
