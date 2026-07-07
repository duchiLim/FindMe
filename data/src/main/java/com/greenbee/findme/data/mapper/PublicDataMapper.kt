package com.greenbee.findme.data.mapper

import com.greenbee.findme.data.remote.dto.AbandonmentItemDto
import com.greenbee.findme.data.remote.dto.KindItemDto
import com.greenbee.findme.data.remote.dto.PublicDataContentDto
import com.greenbee.findme.data.remote.dto.ShelterItemDto
import com.greenbee.findme.data.remote.dto.SidoItemDto
import com.greenbee.findme.data.remote.dto.SigunguItemDto
import com.greenbee.findme.domain.model.AbandonedAnimal
import com.greenbee.findme.domain.model.AnimalKind
import com.greenbee.findme.domain.model.PagedResult
import com.greenbee.findme.domain.model.Region
import com.greenbee.findme.domain.model.Shelter
import com.greenbee.findme.domain.model.UpKind

internal fun AbandonmentItemDto.toDomain(): AbandonedAnimal? {
    val id = desertionNo?.takeIf { it.isNotBlank() } ?: return null
    return AbandonedAnimal(
        desertionNo = id,
        happenPlace = happenPlace.orEmpty(),
        kindName = kindCd.orEmpty(),
        color = colorCd.orEmpty(),
        age = age.orEmpty(),
        weight = weight.orEmpty(),
        imageUrls = toImageUrls(),
        processState = processState.orEmpty(),
        shelterName = careNm.orEmpty(),
        shelterTel = careTel.orEmpty(),
        specialMark = specialMark.orEmpty(),
        sex = sexCd.orEmpty(),
        neuterYn = neuterYn.orEmpty(),
    )
}

internal fun AbandonmentItemDto.toImageUrls(): List<String> =
    buildList {
        popfile1?.takeIf { it.isNotBlank() }?.let(::add)
        popfile2?.takeIf { it.isNotBlank() }?.let(::add)
    }.distinct()

internal fun SidoItemDto.toDomain(): Region? {
    val regionCode = code?.takeIf { it.isNotBlank() } ?: return null
    val regionName = name?.takeIf { it.isNotBlank() } ?: return null
    return Region(code = regionCode, name = regionName)
}

internal fun SigunguItemDto.toDomain(): Region? {
    val regionCode = code?.takeIf { it.isNotBlank() } ?: return null
    val regionName = name?.takeIf { it.isNotBlank() } ?: return null
    return Region(
        code = regionCode,
        name = regionName,
        parentCode = parentCode,
    )
}

internal fun ShelterItemDto.toDomain(): Shelter? {
    val regNo = careRegNo?.takeIf { it.isNotBlank() } ?: return null
    return Shelter(
        careRegNo = regNo,
        name = careName.orEmpty(),
        orgName = orgName.orEmpty(),
        tel = careTel.orEmpty(),
        address = careAddr.orEmpty(),
    )
}

internal fun KindItemDto.toDomain(): AnimalKind? {
    val kindCode = code?.takeIf { it.isNotBlank() } ?: return null
    val kindName = name?.takeIf { it.isNotBlank() } ?: return null
    val upKind = UpKind.entries.firstOrNull { it.code == upKindCode } ?: return null
    return AnimalKind(
        code = kindCode,
        name = kindName,
        upKind = upKind,
    )
}

internal fun <T, R> PublicDataContentDto<T>.toPagedResult(
    items: List<R>,
): PagedResult<R> = PagedResult(
    items = items,
    pageNo = pageNo ?: 1,
    numOfRows = numOfRows ?: items.size,
    totalCount = totalCount ?: items.size,
)
