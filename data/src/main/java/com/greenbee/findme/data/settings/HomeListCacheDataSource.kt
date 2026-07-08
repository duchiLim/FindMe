package com.greenbee.findme.data.settings

import android.content.Context
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.HomeListCache
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Singleton
class HomeListCacheDataSource @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val json = Json { ignoreUnknownKeys = true }

    fun load(): HomeListCache? {
        val raw = preferences.getString(KEY_CACHE, null) ?: return null
        return runCatching {
            json.decodeFromString<HomeListCacheDto>(raw).toDomain()
        }.getOrNull()
    }

    fun save(animals: List<AbandonedAnimal>, totalCount: Int, filterSummary: String) {
        val dto = HomeListCacheDto(
            animals = animals.map { it.toDto() },
            totalCount = totalCount,
            filterSummary = filterSummary,
        )
        preferences.edit()
            .putString(KEY_CACHE, json.encodeToString(dto))
            .apply()
    }

    fun clear() {
        preferences.edit().remove(KEY_CACHE).apply()
    }

    private companion object {
        const val PREFS_NAME = "home_list_cache"
        const val KEY_CACHE = "cache"
    }
}

@Serializable
private data class HomeListCacheDto(
    val animals: List<CachedAbandonedAnimalDto>,
    val totalCount: Int,
    val filterSummary: String,
)

@Serializable
private data class CachedAbandonedAnimalDto(
    val desertionNo: String,
    val happenPlace: String,
    val kindName: String,
    val color: String,
    val age: String,
    val weight: String,
    val imageUrls: List<String>,
    val processState: String,
    val shelterName: String,
    val shelterTel: String,
    val specialMark: String,
    val sex: String,
    val neuterYn: String,
    val happenDate: String = "",
    val noticeStartDate: String = "",
    val noticeEndDate: String = "",
)

private fun HomeListCacheDto.toDomain(): HomeListCache =
    HomeListCache(
        animals = animals.map { it.toDomain() },
        totalCount = totalCount,
        filterSummary = filterSummary,
    )

private fun AbandonedAnimal.toDto(): CachedAbandonedAnimalDto =
    CachedAbandonedAnimalDto(
        desertionNo = desertionNo,
        happenPlace = happenPlace,
        kindName = kindName,
        color = color,
        age = age,
        weight = weight,
        imageUrls = imageUrls,
        processState = processState,
        shelterName = shelterName,
        shelterTel = shelterTel,
        specialMark = specialMark,
        sex = sex,
        neuterYn = neuterYn,
        happenDate = happenDate,
        noticeStartDate = noticeStartDate,
        noticeEndDate = noticeEndDate,
    )

private fun CachedAbandonedAnimalDto.toDomain(): AbandonedAnimal =
    AbandonedAnimal(
        desertionNo = desertionNo,
        happenPlace = happenPlace,
        kindName = kindName,
        color = color,
        age = age,
        weight = weight,
        imageUrls = imageUrls,
        processState = processState,
        shelterName = shelterName,
        shelterTel = shelterTel,
        specialMark = specialMark,
        sex = sex,
        neuterYn = neuterYn,
        happenDate = happenDate,
        noticeStartDate = noticeStartDate,
        noticeEndDate = noticeEndDate,
    )
