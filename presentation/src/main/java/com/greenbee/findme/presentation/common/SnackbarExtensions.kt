package com.greenbee.findme.presentation.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

const val FAVORITE_SNACKBAR_DURATION_MS = 3_000L

suspend fun SnackbarHostState.showSnackbarWithAutoDismiss(
    message: String,
    actionLabel: String? = null,
    durationMs: Long = FAVORITE_SNACKBAR_DURATION_MS,
): SnackbarResult = coroutineScope {
    val autoDismissJob = launch {
        delay(durationMs)
        currentSnackbarData?.dismiss()
    }
    try {
        showSnackbar(
            message = message,
            actionLabel = actionLabel,
        )
    } finally {
        autoDismissJob.cancel()
    }
}
