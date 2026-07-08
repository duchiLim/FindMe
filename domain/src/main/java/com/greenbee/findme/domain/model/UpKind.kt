package com.greenbee.findme.domain.model

enum class UpKind(val code: String) {
    DOG("417000"),
    CAT("422400"),
    ETC("429900"),
    ;

    companion object {
        fun fromStored(value: String?): UpKind? =
            value?.let { stored -> entries.find { it.name == stored } }
    }
}
