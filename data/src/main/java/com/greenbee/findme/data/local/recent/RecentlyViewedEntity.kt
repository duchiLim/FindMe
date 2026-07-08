package com.greenbee.findme.data.local.recent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed_animals")
data class RecentlyViewedEntity(
    @PrimaryKey val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val imageUrl1: String?,
    val imageUrl2: String?,
    val viewedAt: Long,
)
