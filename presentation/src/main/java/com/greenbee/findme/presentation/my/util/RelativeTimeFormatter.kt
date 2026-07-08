package com.greenbee.findme.presentation.my.util

import java.util.concurrent.TimeUnit

fun formatRelativeTime(viewedAt: Long, now: Long = System.currentTimeMillis()): String {
    val diffMs = (now - viewedAt).coerceAtLeast(0)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
    return when {
        minutes < 1 -> "방금 전"
        minutes < 60 -> "${minutes}분 전"
        minutes < 24 * 60 -> "${minutes / 60}시간 전"
        minutes < 48 * 60 -> "어제"
        else -> "${minutes / (24 * 60)}일 전"
    }
}
