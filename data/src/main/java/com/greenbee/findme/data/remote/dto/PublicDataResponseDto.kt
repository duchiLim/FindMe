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
    @Serializable(with = SidoItemListSerializer::class)
    val item: List<SidoItemDto> = emptyList(),
)

private object SidoItemListSerializer : FlexibleItemListSerializer<SidoItemDto>(SidoItemDto.serializer())

@Serializable
data class SidoItemDto(
    @SerialName("orgCd") val code: String? = null,
    @SerialName("orgdownNm") val name: String? = null,
)

@Serializable
data class SigunguItemsDto(
    @Serializable(with = SigunguItemListSerializer::class)
    val item: List<SigunguItemDto> = emptyList(),
)

private object SigunguItemListSerializer : FlexibleItemListSerializer<SigunguItemDto>(SigunguItemDto.serializer())

@Serializable
data class SigunguItemDto(
    @SerialName("orgCd") val code: String? = null,
    @SerialName("orgdownNm") val name: String? = null,
    @SerialName("upr_cd") val parentCode: String? = null,
)

@Serializable
data class ShelterItemsDto(
    @Serializable(with = ShelterItemListSerializer::class)
    val item: List<ShelterItemDto> = emptyList(),
)

private object ShelterItemListSerializer : FlexibleItemListSerializer<ShelterItemDto>(ShelterItemDto.serializer())

@Serializable
data class ShelterItemDto(
    @SerialName("careRegNo") val careRegNo: String? = null,
    @SerialName("careNm") val careName: String? = null,
    @SerialName("orgNm") val orgName: String? = null,
    @SerialName("careTel") val careTel: String? = null,
    @SerialName("careAddr") val careAddr: String? = null,
)

@Serializable
data class KindItemsDto(
    @Serializable(with = KindItemListSerializer::class)
    val item: List<KindItemDto> = emptyList(),
)

private object KindItemListSerializer : FlexibleItemListSerializer<KindItemDto>(KindItemDto.serializer())

@Serializable
data class KindItemDto(
    @SerialName("kindCd") val code: String? = null,
    @SerialName("knm") val name: String? = null,
    @SerialName("upKindCd") val upKindCode: String? = null,
)

@Serializable
data class AbandonmentItemsDto(
    @Serializable(with = AbandonmentItemListSerializer::class)
    val item: List<AbandonmentItemDto> = emptyList(),
)

private object AbandonmentItemListSerializer :
    FlexibleItemListSerializer<AbandonmentItemDto>(AbandonmentItemDto.serializer())

@Serializable
data class AbandonmentItemDto(
    @SerialName("desertionNo") val desertionNo: String? = null,
    @SerialName("happenPlace") val happenPlace: String? = null,
    @SerialName("kindCd") val kindCd: String? = null,
    @SerialName("colorCd") val colorCd: String? = null,
    val age: String? = null,
    val weight: String? = null,
    val popfile: String? = null,
    @SerialName("popfile1") val popfile1: String? = null,
    @SerialName("processState") val processState: String? = null,
    @SerialName("careNm") val careNm: String? = null,
    @SerialName("careTel") val careTel: String? = null,
    @SerialName("specialMark") val specialMark: String? = null,
    @SerialName("sexCd") val sexCd: String? = null,
    @SerialName("neuterYn") val neuterYn: String? = null,
)
