package com.greenbee.findme.presentation.detail.model

import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.presentation.home.model.ProcessStateType
import com.greenbee.findme.presentation.home.model.toProcessStateType

data class AnimalDetailUiModel(
    val desertionNo: String,
    val kindName: String,
    val processState: String,
    val statusType: ProcessStateType,
    val sexLabel: String,
    val neuterLabel: String,
    val age: String,
    val weight: String,
    val color: String,
    val specialMark: String,
    val happenPlace: String,
    val shelterName: String,
    val shelterTel: String,
    val imageUrls: List<String>,
    val canCallShelter: Boolean,
)

fun AbandonedAnimal.toDetailUiModel(): AnimalDetailUiModel {
    val statusType = processState.toProcessStateType()
    return AnimalDetailUiModel(
        desertionNo = desertionNo,
        kindName = kindName.ifBlank { "품종 미상" },
        processState = processState.ifBlank { "상태 미상" },
        statusType = statusType,
        sexLabel = sex.toSexLabel(),
        neuterLabel = neuterYn.toNeuterLabel(),
        age = age.ifBlank { "-" },
        weight = weight.ifBlank { "-" },
        color = color.ifBlank { "-" },
        specialMark = specialMark,
        happenPlace = happenPlace.ifBlank { "-" },
        shelterName = shelterName.ifBlank { "보호소 정보 없음" },
        shelterTel = shelterTel,
        imageUrls = imageUrls,
        canCallShelter = shelterTel.isNotBlank() && statusType != ProcessStateType.ENDED,
    )
}

private fun String.toSexLabel(): String {
    val normalized = trim()
    if (normalized.isBlank()) return "미상"
    return when (normalized.uppercase()) {
        "M", "MALE" -> "수컷"
        "F", "FEMALE" -> "암컷"
        "수컷", "암컷" -> normalized
        else -> normalized
    }
}

private fun String.toNeuterLabel(): String {
    val normalized = trim()
    if (normalized.isBlank()) return "미상"
    return when (normalized.uppercase()) {
        "Y", "YES" -> "예"
        "N", "NO" -> "아니오"
        "예", "아니오" -> normalized
        else -> normalized
    }
}
