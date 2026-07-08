package com.greenbee.findme.data.local.recent

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentlyViewedDao {

    @Query("SELECT * FROM recently_viewed_animals ORDER BY viewedAt DESC LIMIT :limit")
    fun observeRecent(limit: Int): Flow<List<RecentlyViewedEntity>>

    @Query("SELECT * FROM recently_viewed_animals WHERE desertionNo = :desertionNo LIMIT 1")
    suspend fun getByDesertionNo(desertionNo: String): RecentlyViewedEntity?

    @Query("SELECT COUNT(*) FROM recently_viewed_animals")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: RecentlyViewedEntity)

    @Update
    suspend fun update(entity: RecentlyViewedEntity)

    @Query(
        """
        DELETE FROM recently_viewed_animals
        WHERE desertionNo IN (
            SELECT desertionNo FROM recently_viewed_animals
            ORDER BY viewedAt ASC
            LIMIT :count
        )
        """,
    )
    suspend fun deleteOldest(count: Int)
}
