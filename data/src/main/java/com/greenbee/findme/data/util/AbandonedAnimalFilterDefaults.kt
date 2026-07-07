package com.greenbee.findme.data.util

import com.greenbee.findme.domain.model.AbandonedAnimalFilter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.BASIC_ISO_DATE

internal fun AbandonedAnimalFilter.withDefaultDateRange(): AbandonedAnimalFilter {
    if (beginDate != null && endDate != null) return this

    val end = LocalDate.now()
    val begin = end.minusDays(30)

    return copy(
        beginDate = beginDate ?: begin.format(dateFormatter),
        endDate = endDate ?: end.format(dateFormatter),
    )
}
