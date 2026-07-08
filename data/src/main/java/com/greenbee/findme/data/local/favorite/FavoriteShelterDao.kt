package com.greenbee.findme.data.local.favorite

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteShelterDao {

    @Query("SELECT * FROM favorite_shelters ORDER BY savedAt DESC")
    fun observeAll(): Flow<List<FavoriteShelterEntity>>

    @Query("SELECT careRegNo FROM favorite_shelters")
    fun observeCareRegNos(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_shelters WHERE careRegNo = :careRegNo)")
    fun observeIsFavorite(careRegNo: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_shelters WHERE careRegNo = :careRegNo)")
    suspend fun isFavorite(careRegNo: String): Boolean

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: FavoriteShelterEntity)

    @Query("DELETE FROM favorite_shelters WHERE careRegNo = :careRegNo")
    suspend fun delete(careRegNo: String)
}
