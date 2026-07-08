package com.greenbee.findme.data.local.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_shelters")
data class FavoriteShelterEntity(
    @PrimaryKey val careRegNo: String,
    val name: String,
    val orgName: String,
    val tel: String,
    val address: String,
    val savedAt: Long,
)
