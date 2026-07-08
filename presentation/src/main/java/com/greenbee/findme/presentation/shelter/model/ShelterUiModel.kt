package com.greenbee.findme.presentation.shelter.model

import com.greenbee.findme.domain.model.Shelter

data class ShelterUiModel(
    val careRegNo: String,
    val name: String,
    val orgName: String,
    val tel: String,
    val address: String,
    val canCall: Boolean,
)

fun Shelter.toUiModel(): ShelterUiModel = ShelterUiModel(
    careRegNo = careRegNo,
    name = name.ifBlank { "보호소 정보 없음" },
    orgName = orgName,
    tel = tel,
    address = address.ifBlank { "주소 정보 없음" },
    canCall = tel.isNotBlank(),
)
