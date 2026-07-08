package com.greenbee.findme.data.local.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_animals")
data class FavoriteAnimalEntity(
    @PrimaryKey val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val shelterTel: String,
    val imageUrl1: String?,
    val imageUrl2: String?,
    val happenPlace: String,
    val color: String,
    val age: String,
    val weight: String,
    val specialMark: String,
    val sex: String,
    val neuterYn: String,
    val savedAt: Long,
)
