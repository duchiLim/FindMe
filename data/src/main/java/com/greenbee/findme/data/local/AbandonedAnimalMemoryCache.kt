package com.greenbee.findme.data.local

import com.greenbee.findme.domain.model.AbandonedAnimal
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AbandonedAnimalMemoryCache @Inject constructor() {

    private val cache = ConcurrentHashMap<String, AbandonedAnimal>()

    fun put(animal: AbandonedAnimal) {
        cache[animal.desertionNo] = animal
    }

    fun putAll(animals: List<AbandonedAnimal>) {
        animals.forEach(::put)
    }

    fun get(desertionNo: String): AbandonedAnimal? = cache[desertionNo]
}
