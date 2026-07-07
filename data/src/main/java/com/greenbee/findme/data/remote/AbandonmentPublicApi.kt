package com.greenbee.findme.data.remote

import com.greenbee.findme.data.remote.dto.AbandonmentItemsDto
import com.greenbee.findme.data.remote.dto.KindItemsDto
import com.greenbee.findme.data.remote.dto.PublicDataResponseDto
import com.greenbee.findme.data.remote.dto.ShelterItemsDto
import com.greenbee.findme.data.remote.dto.SidoItemsDto
import com.greenbee.findme.data.remote.dto.SigunguItemsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AbandonmentPublicApi {

    @GET("sido_v2")
    suspend fun getSidoList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<SidoItemsDto>

    @GET("sigungu_v2")
    suspend fun getSigunguList(
        @Query("serviceKey") serviceKey: String,
        @Query("upr_cd") sidoCode: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<SigunguItemsDto>

    @GET("shelter_v2")
    suspend fun getShelterList(
        @Query("serviceKey") serviceKey: String,
        @Query("upr_cd") sidoCode: String,
        @Query("org_cd") sigunguCode: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<ShelterItemsDto>

    @GET("kind_v2")
    suspend fun getKindList(
        @Query("serviceKey") serviceKey: String,
        @Query("up_kind_cd") upKindCode: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<KindItemsDto>

    @GET("abandonmentPublic_v2")
    suspend fun getAbandonmentList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("bgnde") beginDate: String? = null,
        @Query("endde") endDate: String? = null,
        @Query("upkind") upKind: String? = null,
        @Query("upr_cd") sidoCode: String? = null,
        @Query("org_cd") sigunguCode: String? = null,
        @Query("state") state: String? = null,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<AbandonmentItemsDto>
}
