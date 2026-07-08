package com.greenbee.findme.domain.model

enum class HomeDateRange(val days: Int) {
    DAYS_7(7),
    DAYS_30(30),
    DAYS_90(90),
    ;

    companion object {
        fun fromStored(value: String?): HomeDateRange =
            value?.let { stored -> entries.find { it.name == stored } } ?: DAYS_30
    }
}
