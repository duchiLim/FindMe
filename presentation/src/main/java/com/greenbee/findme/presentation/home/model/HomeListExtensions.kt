package com.greenbee.findme.presentation.home.model

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val API_DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.BASIC_ISO_DATE

const val HOME_CURATION_LIMIT = 10
const val HOME_ADOPTION_IMMINENT_DAYS = 7L

fun String.parseApiDate(): LocalDate? =
    takeIf { it.length == 8 }
        ?.let { runCatching { LocalDate.parse(it, API_DATE_FORMATTER) }.getOrNull() }

fun AbandonedAnimalUiModel.isRegisteredToday(): Boolean {
    val date = happenDate.parseApiDate() ?: return false
    return date == LocalDate.now()
}

fun AbandonedAnimalUiModel.isAdoptionImminent(): Boolean {
    if (statusType != ProcessStateType.NOTICE) return false
    val endDate = noticeEndDate.parseApiDate() ?: return false
    val daysUntil = ChronoUnit.DAYS.between(LocalDate.now(), endDate)
    return daysUntil in 0..HOME_ADOPTION_IMMINENT_DAYS
}

fun List<AbandonedAnimalUiModel>.filterBySearch(query: String): List<AbandonedAnimalUiModel> {
    val normalizedQuery = query.trim()
    if (normalizedQuery.isBlank()) return this

    return filter { animal ->
        listOf(
            animal.kindName,
            animal.specialMark,
            animal.shelterName,
            animal.happenPlace,
            animal.color,
        ).any { field ->
            field.contains(normalizedQuery, ignoreCase = true)
        }
    }
}

fun List<AbandonedAnimalUiModel>.sortedBy(sortOrder: HomeSortOrder): List<AbandonedAnimalUiModel> =
    when (sortOrder) {
        HomeSortOrder.LATEST -> sortedWith(
            compareByDescending<AbandonedAnimalUiModel> { it.happenDate.parseApiDate() }
                .thenByDescending { it.desertionNo },
        )
        HomeSortOrder.NOTICE_END_SOON -> sortedWith(
            compareBy<AbandonedAnimalUiModel> { animal ->
                val endDate = animal.noticeEndDate.parseApiDate()
                when {
                    animal.statusType != ProcessStateType.NOTICE -> Long.MAX_VALUE
                    endDate == null -> Long.MAX_VALUE - 1
                    else -> ChronoUnit.DAYS.between(LocalDate.now(), endDate)
                }
            }.thenByDescending { it.happenDate.parseApiDate() },
        )
        HomeSortOrder.STATUS_NOTICE_FIRST -> sortedWith(
            compareBy<AbandonedAnimalUiModel> { it.statusType.sortPriority() }
                .thenByDescending { it.happenDate.parseApiDate() },
        )
    }

private fun ProcessStateType.sortPriority(): Int =
    when (this) {
        ProcessStateType.NOTICE -> 0
        ProcessStateType.PROTECT -> 1
        ProcessStateType.UNKNOWN -> 2
        ProcessStateType.ENDED -> 3
    }
