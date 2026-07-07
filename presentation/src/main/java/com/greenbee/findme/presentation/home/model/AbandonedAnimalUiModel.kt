package com.greenbee.findme.presentation.home.model

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.presentation.ui.theme.FindMeStatusEnded
import com.greenbee.findme.presentation.ui.theme.FindMeStatusNotice
import com.greenbee.findme.presentation.ui.theme.FindMeStatusProtect
import androidx.compose.ui.graphics.Color

data class AbandonedAnimalUiModel(
    val desertionNo: String,
    val kindName: String,
    val processState: String,
    val shelterName: String,
    val imageUrls: List<String>,
    val statusType: ProcessStateType,
)

enum class ProcessStateType {
    NOTICE,
    PROTECT,
    ENDED,
    UNKNOWN,
    ;

    val color: Color
        get() = when (this) {
            NOTICE -> FindMeStatusNotice
            PROTECT -> FindMeStatusProtect
            ENDED -> FindMeStatusEnded
            UNKNOWN -> FindMeStatusEnded
        }
}

fun AbandonedAnimal.toUiModel(): AbandonedAnimalUiModel =
    AbandonedAnimalUiModel(
        desertionNo = desertionNo,
        kindName = kindName.ifBlank { "품종 미상" },
        processState = processState.ifBlank { "상태 미상" },
        shelterName = shelterName.ifBlank { "보호소 정보 없음" },
        imageUrls = imageUrls,
        statusType = processState.toProcessStateType(),
    )

fun String.toProcessStateType(): ProcessStateType {
    val normalized = lowercase()
    return when {
        normalized.contains("공고") || normalized.contains("입양") -> ProcessStateType.NOTICE
        normalized.contains("보호") -> ProcessStateType.PROTECT
        normalized.contains("종료") || normalized.contains("반환") || normalized.contains("자연사") ->
            ProcessStateType.ENDED
        else -> ProcessStateType.UNKNOWN
    }
}
