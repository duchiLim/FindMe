package com.greenbee.findme.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicDataResponseDto<T>(
    val response: PublicDataBodyDto<T>,
)

@Serializable
data class PublicDataBodyDto<T>(
    val header: PublicDataHeaderDto,
    val body: PublicDataContentDto<T>,
)

@Serializable
data class PublicDataHeaderDto(
    @SerialName("resultCode") val resultCode: String,
    @SerialName("resultMsg") val resultMsg: String,
)

@Serializable
data class PublicDataContentDto<T>(
    val items: T? = null,
    @SerialName("numOfRows") val numOfRows: Int? = null,
    @SerialName("pageNo") val pageNo: Int? = null,
    @SerialName("totalCount") val totalCount: Int? = null,
)

@Serializable
data class SidoItemsDto(
    val item: List<SidoItemDto>? = null,
)

@Serializable
data class SidoItemDto(
    @SerialName("orgCd") val code: String? = null,
    @SerialName("orgdownNm") val name: String? = null,
)

@Serializable
data class AbandonmentItemsDto(
    val item: List<AbandonmentItemDto>? = null,
)

@Serializable
data class AbandonmentItemDto(
    @SerialName("desertionNo") val desertionNo: String? = null,
    @SerialName("happenPlace") val happenPlace: String? = null,
    @SerialName("kindCd") val kindCd: String? = null,
    @SerialName("colorCd") val colorCd: String? = null,
    val age: String? = null,
    val weight: String? = null,
    val popfile: String? = null,
    @SerialName("processState") val processState: String? = null,
    @SerialName("careNm") val careNm: String? = null,
    @SerialName("careTel") val careTel: String? = null,
    @SerialName("specialMark") val specialMark: String? = null,
)
