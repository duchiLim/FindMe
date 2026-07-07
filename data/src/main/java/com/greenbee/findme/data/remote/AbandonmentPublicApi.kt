package com.greenbee.findme.data.remote

import com.greenbee.findme.data.remote.dto.AbandonmentItemsDto
import com.greenbee.findme.data.remote.dto.PublicDataResponseDto
import com.greenbee.findme.data.remote.dto.SidoItemsDto
import retrofit2.http.GET
import retrofit2.http.Query

interface AbandonmentPublicApi {

    @GET("sido")
    suspend fun getSidoList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<SidoItemsDto>

    @GET("abandonmentPublic")
    suspend fun getAbandonmentList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int,
        @Query("pageNo") pageNo: Int,
        @Query("bgnde") beginDate: String? = null,
        @Query("endde") endDate: String? = null,
        @Query("upkind") upKind: String? = null,
        @Query("state") state: String? = null,
        @Query("_type") responseType: String = "json",
    ): PublicDataResponseDto<AbandonmentItemsDto>
}
